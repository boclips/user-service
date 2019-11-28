package com.boclips.users.domain.service

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.user.UserExpired
import com.boclips.users.domain.service.events.EventConverter
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.nhaarman.mockitokotlin2.check
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class AccessServiceTest : AbstractSpringIntegrationTest() {

    @Test
    fun `it allows a user with lifetime access`() {
        val user = UserFactory.sample(
            accessExpiry = null
        )
        assertThat(accessService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it allows a user with expiry date in the future`() {
        val user = UserFactory.sample(
            accessExpiry = ZonedDateTime.now().plusDays(1)
        )
        assertThat(accessService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it disallows a user with expiry date in the past`() {
        val user = UserFactory.sample(
            accessExpiry = ZonedDateTime.now().minusDays(1)
        )
        assertThat(accessService.userHasAccess(user)).isEqualTo(false)
    }

    @Test
    fun `it allows a user with expiry date in the past, but district expiry in the future`() {
        val district = organisationAccountRepository.save(
            OrganisationFactory.district(
                accessExpiry = ZonedDateTime.now().plusMonths(3)
            )
        )
        val school = OrganisationFactory.school(district = district)
        val schoolAccount = organisationAccountRepository.save(school)

        val user = UserFactory.sample(
            accessExpiry = ZonedDateTime.now().minusDays(1),
            organisationAccountId = schoolAccount.id
        )

        assertThat(accessService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it allows a user with expiry date in the future, but district expiry in the past`() {
        val district = organisationAccountRepository.save(
            OrganisationFactory.district(
                accessExpiry = ZonedDateTime.now().minusMonths(3)
            )
        )
        val school = OrganisationFactory.school(district = district)
        val schoolAccount = organisationAccountRepository.save(school)

        val user = UserFactory.sample(
            organisationAccountId = schoolAccount.id,
            accessExpiry = ZonedDateTime.now().plusDays(10)
        )

        assertThat(accessService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it allows a lifetime user, but district expiry in the past`() {
        val district = organisationAccountRepository.save(
            OrganisationFactory.district(
                accessExpiry = ZonedDateTime.now().minusMonths(3)
            )
        )
        val school = OrganisationFactory.school(district = district)
        val schoolAccount = organisationAccountRepository.save(school)

        val user = UserFactory.sample(
            organisationAccountId = schoolAccount.id,
            accessExpiry = null
        )

        assertThat(accessService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it disallows a user with expiry date in the past, and a district expiry in the past`() {
        val district = organisationAccountRepository.save(
            OrganisationFactory.district(
                accessExpiry = ZonedDateTime.now().minusMonths(3)
            )
        )
        val school = OrganisationFactory.school(district = district)
        val schoolAccount = organisationAccountRepository.save(school)

        val user = UserFactory.sample(
            organisationAccountId = schoolAccount.id,
            accessExpiry = ZonedDateTime.now().minusMonths(10)
        )

        assertThat(accessService.userHasAccess(user)).isEqualTo(false)
    }

    @Test
    fun `it emits a UserExpired event when the user has expired`() {
        val mockEventBus: EventBus = mock()
        val eventConverter = EventConverter(organisationAccountRepository = organisationAccountRepository)

        val accessService = AccessService(
            organisationAccountRepository = organisationAccountRepository,
            eventBus = mockEventBus,
            eventConverter = eventConverter
        )

        val user = UserFactory.sample(
            account = AccountFactory.sample(
                id = "user-id"
            ),
            accessExpiry = ZonedDateTime.now().minusDays(1)
        )

        assertThat(accessService.userHasAccess(user)).isEqualTo(false)
        verify(mockEventBus).publish(check<UserExpired> {
            assertThat(it).isInstanceOf(UserExpired::class.java)
            assertThat((it).user.id).isEqualTo("user-id")
        })
    }
}
