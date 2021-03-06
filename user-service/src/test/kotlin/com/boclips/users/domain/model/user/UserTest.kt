package com.boclips.users.domain.model.user

import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class UserTest {

    @Test
    fun `returns features assigned to organisation`() {
        // given
        val user = UserFactory.sample(
            organisation = OrganisationFactory.school(
                features = mapOf(
                    Feature.BO_WEB_APP_ADDITIONAL_SERVICES to false,
                    Feature.LTI_RESPONSIVE_VIDEO_CARD to true
                )
            )
        )

        // when
        val features = user.features

        // then
        assertThat(features).isEqualTo(
            mapOf(
                Feature.LTI_SLS_TERMS_BUTTON to false,
                Feature.LTI_AGE_FILTER to true,
                Feature.LTI_RESPONSIVE_VIDEO_CARD to true,
                Feature.USER_DATA_HIDDEN to false,
                Feature.BO_WEB_APP_COPY_OLD_LINK_BUTTON to false,
                Feature.BO_WEB_APP_ADDITIONAL_SERVICES to false,
                Feature.BO_WEB_APP_PRICES to true
            )
        )
    }

    @Test
    fun `resolves features to default ones when organisation has no features configured`() {
        // given
        val user = UserFactory.sample(organisation = OrganisationFactory.school(features = null))

        // when
        val features = user.features

        // then
        assertThat(features).isEqualTo(Feature.DEFAULT_VALUES)
    }

    @Test
    fun `resolves features to default ones when no organisation is assigned to a user`() {
        // given
        val user = UserFactory.sample(organisation = null)

        // when
        val features = user.features

        // then
        assertThat(features).isEqualTo(Feature.DEFAULT_VALUES)
    }

    @Test
    fun `resolves explicitly assigned access expiration date`() {
        // given
        val user = UserFactory.sample(accessExpiresOn = REFERENCE_NOW)

        // when
        val accessExpiresOn = user.accessExpiresOn

        // then
        assertThat(accessExpiresOn).isEqualTo(REFERENCE_NOW)
    }

    @Test
    fun `organisation expiration date takes precedence over the date explicitly assigned to a user`() {
        // given
        val user = UserFactory.sample(
            accessExpiresOn = YESTERDAY,
            organisation = OrganisationFactory.district(
                deal = Deal(billing = true, accessExpiresOn = REFERENCE_NOW)
            )
        )

        // when
        val accessExpiresOn = user.accessExpiresOn

        // then
        assertThat(accessExpiresOn).isEqualTo(REFERENCE_NOW)
    }

    @Test
    fun `returns null when expiration date is set neither for the user nor for the organisation`() {
        // given
        val user = UserFactory.sample(
            accessExpiresOn = null,
            organisation = OrganisationFactory.district(
                deal = Deal(billing = true, accessExpiresOn = null)
            )
        )

        // when
        val accessExpiresOn = user.accessExpiresOn

        // then
        assertThat(accessExpiresOn).isNull()
    }

    @Test
    fun `teacher users are not trackable if they haven't onboarded`() {
        val user = UserFactory.sample(
            identity = IdentityFactory.sample(id = "testUser"),
            profile = null,
            organisation = OrganisationFactory.school()
        )

        assertThat(user.isTrackable()).isFalse
    }

    @Test
    fun `teacher users are trackable if they have onboarded`() {
        val user = UserFactory.sample(
            identity = IdentityFactory.sample(id = "testUser"),
            profile = ProfileFactory.sample(firstName = "Mr Trackable"),
            organisation = OrganisationFactory.school()
        )

        assertThat(user.isTrackable()).isTrue
    }


    @Test
    fun `API users are always trackable`() {
        val user = UserFactory.sample(
            identity = IdentityFactory.sample(id = "apiUser"),
            profile = null,
            organisation = OrganisationFactory.apiIntegration()
        )

        assertThat(user.isTrackable()).isTrue
    }

    @Nested
    inner class IsATeacher {
        @Test
        fun `SCHOOL users are Teachers `() {
            val user = UserFactory.sample(
                organisation = OrganisationFactory.school()
            )

            assertThat(user.isATeacher()).isTrue
        }

        @Test
        fun `District users are Teachers `() {
            val user = UserFactory.sample(
                organisation = OrganisationFactory.district()
            )

            assertThat(user.isATeacher()).isTrue
        }

        @Test
        fun `API users are not Teachers `() {
            val user = UserFactory.sample(
                organisation = OrganisationFactory.apiIntegration()
            )

            assertThat(user.isATeacher()).isFalse
        }

        @Test
        fun `LTI users are not Teachers `() {
            val user = UserFactory.sample(
                organisation = OrganisationFactory.ltiDeployment()
            )

            assertThat(user.isATeacher()).isFalse
        }

        @Test
        fun `Users with no organisation assigned are not Teachers `() {
            val user = UserFactory.sample(
                organisation = null
            )

            assertThat(user.isATeacher()).isFalse
        }
    }

    private companion object {

        val REFERENCE_NOW: ZonedDateTime = ZonedDateTime.now()
        val YESTERDAY: ZonedDateTime = REFERENCE_NOW.minusDays(1)
    }
}
