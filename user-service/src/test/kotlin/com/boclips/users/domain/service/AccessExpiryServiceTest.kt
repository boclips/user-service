package com.boclips.users.domain.service

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
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
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.district(),
                accessExpiresOn = ZonedDateTime.now().plusMonths(3)
            )
        )
        val school = OrganisationDetailsFactory.school(district = district)
        val schoolAccount = organisationRepository.save(OrganisationFactory.sample(details = school))

        val user = UserFactory.sample(
            accessExpiresOn = ZonedDateTime.now().minusDays(1),
            organisationId = schoolAccount.id
        )

        assertThat(accessExpiryService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it allows a user with expiry date in the future, but district expiry in the past`() {
        val district = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.district(),
                accessExpiresOn = ZonedDateTime.now().minusMonths(3)
            )
        )

        val school = OrganisationDetailsFactory.school(district = district)
        val schoolAccount = organisationRepository.save(OrganisationFactory.sample(details = school))

        val user = UserFactory.sample(
            organisationId = schoolAccount.id,
            accessExpiresOn = ZonedDateTime.now().plusDays(10)
        )

        assertThat(accessExpiryService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it allows a lifetime user, but district expiry in the past`() {
        val district = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.district(),
                accessExpiresOn = ZonedDateTime.now().minusMonths(3)
            )
        )
        val school = OrganisationDetailsFactory.school(district = district)
        val schoolAccount = organisationRepository.save(OrganisationFactory.sample(details = school))

        val user = UserFactory.sample(
            organisationId = schoolAccount.id,
            accessExpiresOn = null
        )

        assertThat(accessExpiryService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it disallows a user with expiry date in the past, and a district expiry in the past`() {
        val district = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.district(),
                accessExpiresOn = ZonedDateTime.now().minusMonths(3)
            )
        )
        val school = OrganisationDetailsFactory.school(district = district)
        val schoolAccount = organisationRepository.save(OrganisationFactory.sample(details = school))

        val user = UserFactory.sample(
            organisationId = schoolAccount.id,
            accessExpiresOn = ZonedDateTime.now().minusMonths(10)
        )

        assertThat(accessExpiryService.userHasAccess(user)).isEqualTo(false)
    }
}