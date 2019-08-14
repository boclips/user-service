package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.organisation.OrganisationId
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
                    associatedTo = OrganisationId(value = "some-org-id"),
                    analyticsId = AnalyticsId(value = "some-analytics-id")
                )
            )

        assertThat(userResource.id).isNotNull()
        assertThat(userResource.firstName).isEqualTo("Thierry")
        assertThat(userResource.lastName).isEqualTo("Henry")
        assertThat(userResource.analyticsId).isEqualTo("some-analytics-id")
        assertThat(userResource.email).isEqualTo("thierry@henry.fr")
    }
}
