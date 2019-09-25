package com.boclips.users.presentation.resources

import com.boclips.users.application.converters.UserConverter
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.OrganisationAccountFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
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
                        country = Country(id = "US", name = "United States")
                    ),
                    analyticsId = AnalyticsId(value = "some-analytics-id"),
                    organisationAccountId = OrganisationAccountId("1234")
                ),
                organisationAccount = OrganisationAccountFactory.sample(
                    id = OrganisationAccountId("1234"),
                    organisation = OrganisationFactory.school(
                        name = "My school",
                        state = State.fromCode("NY"),
                        country = Country.fromCode("USA")
                    )
                )
            )

        assertThat(userResource.id).isNotNull()
        assertThat(userResource.firstName).isEqualTo("Thierry")
        assertThat(userResource.lastName).isEqualTo("Henry")
        assertThat(userResource.ages).containsExactly(1, 2, 3)
        assertThat(userResource.subjects).containsExactly("subject-id")
        assertThat(userResource.analyticsId).isEqualTo("some-analytics-id")
        assertThat(userResource.email).isEqualTo("thierry@henry.fr")
        assertThat(userResource.organisationAccountId).isEqualTo("1234")
        assertThat(userResource.organisation!!.name).isEqualTo("My school")
        assertThat(userResource.organisation!!.state).isEqualTo("New York")
        assertThat(userResource.organisation!!.country).isEqualTo("United States")
    }

    @Test
    fun `converts a user without an organisation`() {
        val userResource =
            UserConverter().toUserResource(
                user = UserFactory.sample(
                    organisationAccountId = null
                ),
                organisationAccount = null
            )

        assertThat(userResource.organisation).isNull()
    }
}
