package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
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
                        lastName = "Henry",
                        ages = listOf(1, 2, 3),
                        subjects = listOf(Subject(SubjectId("subject-id"), name = "Math")),
                        country = Country(id = "US", name = "United States"),
                        state = State(id = "NY", name = "New York"),
                        school = "Some School"
                    ),
                    analyticsId = AnalyticsId(value = "some-analytics-id")
                )
            )

        assertThat(userResource.id).isNotNull()
        assertThat(userResource.firstName).isEqualTo("Thierry")
        assertThat(userResource.lastName).isEqualTo("Henry")
        assertThat(userResource.ages).containsExactly(1, 2, 3)
        assertThat(userResource.subjects).containsExactly("subject-id")
        assertThat(userResource.country!!.id).isEqualTo("US")
        assertThat(userResource.country!!.name).isEqualTo("United States")
        assertThat(userResource.state!!.id).isEqualTo("NY")
        assertThat(userResource.state!!.name).isEqualTo("New York")
        assertThat(userResource.school).isEqualTo("Some School")
        assertThat(userResource.analyticsId).isEqualTo("some-analytics-id")
        assertThat(userResource.email).isEqualTo("thierry@henry.fr")
    }
}
