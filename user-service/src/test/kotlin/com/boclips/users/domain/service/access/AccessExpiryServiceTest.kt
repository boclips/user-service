package com.boclips.users.domain.service.access

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.deal
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.district
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class AccessExpiryServiceTest : AbstractSpringIntegrationTest() {

    @Test
    fun `it allows a user with lifetime access`() {
        val user = UserFactory.sample(
            accessExpiresOn = null
        )
        assertThat(accessExpiryService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it allows a user with expiry date in the future`() {
        val user = UserFactory.sample(
            accessExpiresOn = ZonedDateTime.now().plusDays(1)
        )
        assertThat(accessExpiryService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it disallows a user with expiry date in the past`() {
        val user = UserFactory.sample(
            accessExpiresOn = ZonedDateTime.now().minusDays(1)
        )
        assertThat(accessExpiryService.userHasAccess(user)).isEqualTo(false)
    }

    @Test
    fun `it allows a user with expiry date in the past, but district expiry in the future`() {
        val district = organisationRepository.save(
            district(
                deal = deal(
                    accessExpiresOn = ZonedDateTime.now().plusMonths(3)
                )
            )
        )
        val school = OrganisationFactory.school(district = district)

        val user = UserFactory.sample(
            accessExpiresOn = ZonedDateTime.now().minusDays(1),
            organisation = school
        )

        assertThat(accessExpiryService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it allows a user with expiry date in the future, but district expiry in the past`() {
        val district = organisationRepository.save(
            district(
                deal = deal(
                    accessExpiresOn = ZonedDateTime.now().minusMonths(3)
                )
            )
        )

        val school = OrganisationFactory.school(district = district)
        val schoolAccount = organisationRepository.save(school)

        val user = UserFactory.sample(
            organisation = schoolAccount,
            accessExpiresOn = ZonedDateTime.now().plusDays(10)
        )

        assertThat(accessExpiryService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it allows a lifetime user, but district expiry in the past`() {
        val district = organisationRepository.save(
            district(
                deal = deal(
                    accessExpiresOn = ZonedDateTime.now().minusMonths(3)
                )
            )
        )
        val school = OrganisationFactory.school(district = district)
        val schoolAccount = organisationRepository.save(school)

        val user = UserFactory.sample(
            organisation = schoolAccount,
            accessExpiresOn = null
        )

        assertThat(accessExpiryService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it disallows a user with expiry date in the past, and a district expiry in the past`() {
        val district = organisationRepository.save(
            district(
                deal = deal(
                    accessExpiresOn = ZonedDateTime.now().minusMonths(3)
                )
            )
        )
        val school = OrganisationFactory.school(district = district)
        val schoolAccount = organisationRepository.save(school)

        val user = UserFactory.sample(
            organisation = schoolAccount,
            accessExpiresOn = ZonedDateTime.now().minusMonths(10)
        )

        assertThat(accessExpiryService.userHasAccess(user)).isEqualTo(false)
    }
}
