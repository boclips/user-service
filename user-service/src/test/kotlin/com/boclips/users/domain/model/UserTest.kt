package com.boclips.users.domain.model

import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class UserTest {
    @Test
    fun `the user has onboarded when they have a firstname`() {
        val user = UserFactory.sample(
            profile = ProfileFactory.sample(firstName = "Rebecca")
        )

        assertThat(user.hasOnboarded()).isTrue()
    }

    @Test
    fun `the user has not onboarded when they do not have a first name`() {
        val user = UserFactory.sample(
            profile = ProfileFactory.sample(firstName = "")
        )

        assertThat(user.hasOnboarded()).isFalse()
    }

    @Test
    fun `the user has been activated when they have given us their name`() {
        val user = UserFactory.sample(
            profile = ProfileFactory.sample(firstName = "Dummy")
        )

        assertThat(user.hasOnboarded()).isTrue()
    }

    @Test
    fun `user data should be hidden if USER_DATA_HIDDEN is set`() {
        val user =
            UserFactory.sample(organisation = OrganisationFactory.school(features = mapOf(Feature.USER_DATA_HIDDEN to true)))

        assertThat(user.hasDetailsHidden()).isTrue()
    }

    @Test
    fun `user data is not hidden by default`() {
        val user = UserFactory.sample()

        assertThat(user.hasDetailsHidden()).isFalse()
    }

    @Test
    fun `the user is not considered activated without profile information`() {
        val user = UserFactory.sample(
            profile = ProfileFactory.sample(firstName = "")
        )

        assertThat(user.hasOnboarded()).isFalse()
    }

    @Nested
    inner class AccessExpiresOn {
        @Test
        fun `it takes the furthest date in the future when both user and organisation have an expiry date`() {
            val userAccess = ZonedDateTime.now()
            val orgAccess = ZonedDateTime.now().plusDays(10)

            val user = UserFactory.sample(
                organisation = OrganisationFactory.school(deal = OrganisationFactory.deal(accessExpiresOn = orgAccess)),
                accessExpiresOn = userAccess
            )

            assertThat(user.accessExpiresOn).isEqualTo(orgAccess)
        }

        @Test
        fun `district deal expiry date takes precedent over school expiry date`() {
            val userAccess = ZonedDateTime.now()
            val schoolAccess = ZonedDateTime.now().plusDays(10)
            val districtAccess = ZonedDateTime.now().plusDays(15)

            val user = UserFactory.sample(
                organisation = OrganisationFactory.school(
                    district = OrganisationFactory.district(deal = OrganisationFactory.deal(accessExpiresOn = districtAccess)),
                    deal = OrganisationFactory.deal(accessExpiresOn = schoolAccess)
                ),
                accessExpiresOn = userAccess
            )

            assertThat(user.accessExpiresOn).isEqualTo(districtAccess)
        }

        @Test
        fun `it takes the user expiry date when user has no organisation`() {
            val userAccess = ZonedDateTime.now().plusDays(10)

            val user = UserFactory.sample(
                organisation = null,
                accessExpiresOn = userAccess
            )

            assertThat(user.accessExpiresOn).isEqualTo(userAccess)
        }

        @Test
        fun `it returns null when user has no expiry date`() {
            val userAccess = ZonedDateTime.now().plusDays(10)

            val user = UserFactory.sample(
                organisation = null,
                accessExpiresOn = userAccess
            )

            assertThat(user.accessExpiresOn).isEqualTo(userAccess)
        }
    }
}
