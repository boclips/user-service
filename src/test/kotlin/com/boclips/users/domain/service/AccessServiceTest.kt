package com.boclips.users.domain.service

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class AccessServiceTest : AbstractSpringIntegrationTest() {

    @Test
    fun `it allows a user with lifetime access`() {
        val user = UserFactory.sample(
            accessExpiresOn = null
        )
        assertThat(accessService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it allows a user with expiry date in the future`() {
        val user = UserFactory.sample(
            accessExpiresOn = ZonedDateTime.now().plusDays(1)
        )
        assertThat(accessService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it disallows a user with expiry date in the past`() {
        val user = UserFactory.sample(
            accessExpiresOn = ZonedDateTime.now().minusDays(1)
        )
        assertThat(accessService.userHasAccess(user)).isEqualTo(false)
    }

    @Test
    fun `it allows a user with expiry date in the past, but district expiry in the future`() {
        val district = organisationAccountRepository.save(
            OrganisationFactory.district(
                accessExpiresOn = ZonedDateTime.now().plusMonths(3)
            )
        )
        val school = OrganisationFactory.school(district = district)
        val schoolAccount = organisationAccountRepository.save(school)

        val user = UserFactory.sample(
            accessExpiresOn = ZonedDateTime.now().minusDays(1),
            organisationAccountId = schoolAccount.id
        )

        assertThat(accessService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it allows a user with expiry date in the future, but district expiry in the past`() {
        val district = organisationAccountRepository.save(
            OrganisationFactory.district(
                accessExpiresOn = ZonedDateTime.now().minusMonths(3)
            )
        )
        val school = OrganisationFactory.school(district = district)
        val schoolAccount = organisationAccountRepository.save(school)

        val user = UserFactory.sample(
            organisationAccountId = schoolAccount.id,
            accessExpiresOn = ZonedDateTime.now().plusDays(10)
        )

        assertThat(accessService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it allows a lifetime user, but district expiry in the past`() {
        val district = organisationAccountRepository.save(
            OrganisationFactory.district(
                accessExpiresOn = ZonedDateTime.now().minusMonths(3)
            )
        )
        val school = OrganisationFactory.school(district = district)
        val schoolAccount = organisationAccountRepository.save(school)

        val user = UserFactory.sample(
            organisationAccountId = schoolAccount.id,
            accessExpiresOn = null
        )

        assertThat(accessService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it disallows a user with expiry date in the past, and a district expiry in the past`() {
        val district = organisationAccountRepository.save(
            OrganisationFactory.district(
                accessExpiresOn = ZonedDateTime.now().minusMonths(3)
            )
        )
        val school = OrganisationFactory.school(district = district)
        val schoolAccount = organisationAccountRepository.save(school)

        val user = UserFactory.sample(
            organisationAccountId = schoolAccount.id,
            accessExpiresOn = ZonedDateTime.now().minusMonths(10)
        )

        assertThat(accessService.userHasAccess(user)).isEqualTo(false)
    }
}
