package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.Platform
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserConverterTest {
    @Test
    fun `convert user and organisation`() {
        val userResource =
            UserConverter().toUserResource(
                user = UserFactory.sample(
                    account = AccountFactory.sample(
                        username = "thierry@henry.fr"
                    ),
                    profile = ProfileFactory.sample(
                        firstName = "Thierry",
                        lastName = "Henry"
                    ),
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
        val userResource =
            UserConverter().toUserResource(user = UserFactory.sample(account = AccountFactory.sample(platform = Platform.BoclipsForTeachers)))

        assertThat(userResource.organisationId).isNull()
    }

    @Test
    fun `converts users with ApiClient source accordingly`() {
        val userResource =
            UserConverter().toUserResource(
                user = UserFactory.sample(
                    account = AccountFactory.sample(
                        platform = Platform.ApiCustomer(
                            organisationId = com.boclips.users.domain.model.organisation.OrganisationId("test")
                        )
                    )
                )
            )

        assertThat(userResource.organisationId).isEqualTo("test")
    }
}
