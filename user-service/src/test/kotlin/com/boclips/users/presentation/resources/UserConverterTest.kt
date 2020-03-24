package com.boclips.users.presentation.resources

import com.boclips.users.api.response.SubjectResource
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.presentation.hateoas.UserLinkBuilder
import com.boclips.users.presentation.resources.converters.UserConverter
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.TeacherPlatformAttributesFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserConverterTest {
    private lateinit var userConverter: UserConverter

    @BeforeEach()
    fun setUp() {
        userConverter = UserConverter(UserLinkBuilder())
    }

    @Test
    fun `convert user and organisation`() {
        val userResource =
            userConverter.toUserResource(
                user = UserFactory.sample(
                    identity = IdentityFactory.sample(
                        username = "thierry@henry.fr"
                    ),
                    profile = ProfileFactory.sample(
                        firstName = "Thierry",
                        lastName = "Henry",
                        ages = listOf(1, 2, 3),
                        subjects = listOf(Subject(SubjectId("subject-id"), name = "Math"))
                    ),
                    analyticsId = AnalyticsId(value = "some-analytics-id"),
                    organisationId = OrganisationId("1234")
                ),
                organisation = OrganisationFactory.sample(
                    id = OrganisationId("1234"),
                    details = OrganisationDetailsFactory.school(
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
        assertThat(userResource.subjects).containsExactly(
            SubjectResource(
                "subject-id"
            )
        )
        assertThat(userResource.analyticsId).isEqualTo("some-analytics-id")
        assertThat(userResource.email).isEqualTo("thierry@henry.fr")
        assertThat(userResource.organisationAccountId).isEqualTo("1234")
        assertThat(userResource.organisation!!.name).isEqualTo("My school")
        assertThat(userResource.organisation!!.state!!.name).isEqualTo("New York")
        assertThat(userResource.organisation!!.state!!.id).isEqualTo("NY")
        assertThat(userResource.organisation!!.country!!.name).isEqualTo("United States")
        assertThat(userResource.organisation!!.country!!.id).isEqualTo("USA")
    }

    @Test
    fun `converts teachers platform specific fields`() {
        val user = UserFactory.sample(
            teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(shareCode = "TRWN"),
            organisationId = OrganisationId("1234")
        )

        val organisationAccount = OrganisationFactory.sample(
            id = OrganisationId("1234"),
            details = OrganisationDetailsFactory.school(
                name = "My school",
                state = State.fromCode("NY"),
                country = Country.fromCode("USA")
            )
        )

        val userResource = userConverter.toUserResource(user, organisationAccount)

        assertThat(userResource.teacherPlatformAttributes).isNotNull
        assertThat(userResource.teacherPlatformAttributes!!.shareCode).isEqualTo("TRWN")
    }

    @Test
    fun `converts a user without an organisation`() {
        val userResource =
            userConverter.toUserResource(
                user = UserFactory.sample(
                    organisationId = null
                ),
                organisation = null
            )

        assertThat(userResource.organisation).isNull()
    }
}