package com.boclips.users.domain.model.user

import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class UserTest {

    @Test
    fun `returns features assigned to organisation`() {
        // given
        val user = UserFactory.sample(
            organisation = OrganisationFactory.school(
                features = mapOf(
                    Feature.TEACHERS_HOME_BANNER to false,
                    Feature.LTI_RESPONSIVE_VIDEO_CARD to true
                )
            )
        )

        // when
        val features = user.features

        // then
        assertThat(features).isEqualTo(
            mapOf(
                Feature.LTI_COPY_RESOURCE_LINK to false,
                Feature.LTI_SLS_TERMS_BUTTON to false,
                Feature.LTI_RESPONSIVE_VIDEO_CARD to true,
                Feature.TEACHERS_HOME_BANNER to false,
                Feature.TEACHERS_HOME_SUGGESTED_VIDEOS to true,
                Feature.TEACHERS_HOME_PROMOTED_COLLECTIONS to true,
                Feature.TEACHERS_SUBJECTS to true,
                Feature.USER_DATA_HIDDEN to false
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

    private companion object {

        val REFERENCE_NOW: ZonedDateTime = ZonedDateTime.now()
        val YESTERDAY: ZonedDateTime = REFERENCE_NOW.minusDays(1)
    }
}
