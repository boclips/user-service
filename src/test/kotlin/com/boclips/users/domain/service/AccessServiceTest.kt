package com.boclips.users.domain.service

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
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
        val district = accountRepository.save(
            OrganisationDetailsFactory.district(), accessExpiresOn = ZonedDateTime.now().plusMonths(3)
        )
        val school = OrganisationDetailsFactory.school(district = district)
        val schoolAccount = accountRepository.save(school)

        val user = UserFactory.sample(
            accessExpiresOn = ZonedDateTime.now().minusDays(1),
            organisationId = schoolAccount.id
        )

        assertThat(accessService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it allows a user with expiry date in the future, but district expiry in the past`() {
        val district = accountRepository.save(
            OrganisationDetailsFactory.district(),
            accessExpiresOn = ZonedDateTime.now().minusMonths(3)
        )
        val school = OrganisationDetailsFactory.school(district = district)
        val schoolAccount = accountRepository.save(school)

        val user = UserFactory.sample(
            organisationId = schoolAccount.id,
            accessExpiresOn = ZonedDateTime.now().plusDays(10)
        )

        assertThat(accessService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it allows a lifetime user, but district expiry in the past`() {
        val district = accountRepository.save(
            OrganisationDetailsFactory.district(), accessExpiresOn = ZonedDateTime.now().minusMonths(3)
        )
        val school = OrganisationDetailsFactory.school(district = district)
        val schoolAccount = accountRepository.save(school)

        val user = UserFactory.sample(
            organisationId = schoolAccount.id,
            accessExpiresOn = null
        )

        assertThat(accessService.userHasAccess(user)).isEqualTo(true)
    }

    @Test
    fun `it disallows a user with expiry date in the past, and a district expiry in the past`() {
        val district = accountRepository.save(
            OrganisationDetailsFactory.district(), accessExpiresOn = ZonedDateTime.now().minusMonths(3)
        )
        val school = OrganisationDetailsFactory.school(district = district)
        val schoolAccount = accountRepository.save(school)

        val user = UserFactory.sample(
            organisationId = schoolAccount.id,
            accessExpiresOn = ZonedDateTime.now().minusMonths(10)
        )

        assertThat(accessService.userHasAccess(user)).isEqualTo(false)
    }
}
