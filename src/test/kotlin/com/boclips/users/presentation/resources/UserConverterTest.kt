package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.UserSource
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserConverterTest {
    @Test
    fun `convert user and organisation`() {
        val userResource =
            UserConverter().toUserResource(
                user = UserFactory.sample(
                    firstName = "Thierry",
                    lastName = "Henry",
                    email = "thierry@henry.fr",
                    activated = true,
                    analyticsId = AnalyticsId(value = "some-analytics-id")
                )
            )

        assertThat(userResource.id).isNotNull()
        assertThat(userResource.firstName).isEqualTo("Thierry")
        assertThat(userResource.lastName).isEqualTo("Henry")
        assertThat(userResource.analyticsId).isEqualTo("some-analytics-id")
        assertThat(userResource.email).isEqualTo("thierry@henry.fr")
    }

    @Test
    fun `converts users with Boclips source accordingly`() {
        val userResource = UserConverter().toUserResource(user = UserFactory.sample(userSource = UserSource.Boclips))

        assertThat(userResource.organisationId).isNull()
    }

    @Test
    fun `converts users with ApiClient source accordingly`() {
        val userResource =
            UserConverter().toUserResource(
                user = UserFactory.sample(
                    userSource = UserSource.ApiClient(
                        organisationId = com.boclips.users.domain.model.organisation.OrganisationId("test")
                    )
                )
            )

        assertThat(userResource.organisationId).isEqualTo("test")
    }
}
