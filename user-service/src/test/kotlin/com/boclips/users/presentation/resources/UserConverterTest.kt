package com.boclips.users.presentation.resources

import com.boclips.users.api.response.SubjectResource
import com.boclips.users.domain.model.subject.Subject
import com.boclips.users.domain.model.subject.SubjectId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.presentation.converters.UserConverter
import com.boclips.users.presentation.hateoas.UserLinkBuilder
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.TeacherPlatformAttributesFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserConverterTest : AbstractSpringIntegrationTest(){
    private lateinit var userConverter: UserConverter

    @BeforeEach()
    fun setUp() {
        userConverter =
            UserConverter(UserLinkBuilder())
    }

    @Test
    fun `convert user and organisation`() {
        val user = UserFactory.sample(

            identity = IdentityFactory.sample(
                username = "thierry@henry.fr"
            ),
            profile = ProfileFactory.sample(
                firstName = "Thierry",
                lastName = "Henry",
                ages = listOf(1, 2, 3),
                school = OrganisationFactory.school(
                    name = "Elm Street School"
                ),
                subjects = listOf(
                    Subject(
                        SubjectId("subject-id"),
                        name = "Math"
                    )
                )
            ),
            analyticsId = AnalyticsId(value = "some-analytics-id"),
            organisation = OrganisationFactory.school(
                id = OrganisationId(),
                name = "My school",
                address = Address(

                    state = State.fromCode("NY"),
                    country = Country.fromCode("USA")
                ),
                features = mapOf(Feature.TEACHERS_HOME_BANNER to true)
            ))

        saveUser(user)

        val userResource = userConverter.toUserResource(user = user)

        assertThat(userResource.id).isNotNull()
        assertThat(userResource.firstName).isEqualTo("Thierry")
        assertThat(userResource.lastName).isEqualTo("Henry")
        assertThat(userResource.ages).containsExactly(1, 2, 3)
        assertThat(userResource.subjects).containsExactly(
            SubjectResource(
                "subject-id"
            )
        )
        assertThat(userResource.school?.name).isEqualTo("Elm Street School")
        assertThat(userResource.analyticsId).isEqualTo("some-analytics-id")
        assertThat(userResource.email).isEqualTo("thierry@henry.fr")
        assertThat(userResource.organisation!!.id).isEqualTo(user.organisation?.id?.value)
        assertThat(userResource.organisation!!.name).isEqualTo("My school")
        assertThat(userResource.organisation!!.state!!.name).isEqualTo("New York")
        assertThat(userResource.organisation!!.state!!.id).isEqualTo("NY")
        assertThat(userResource.organisation!!.country!!.name).isEqualTo("United States")
        assertThat(userResource.organisation!!.country!!.id).isEqualTo("USA")
        assertThat(userResource.features!!["TEACHERS_HOME_BANNER"]).isEqualTo(true)
    }

    @Test
    fun `converts teachers platform specific fields`() {
        val user = UserFactory.sample(
            teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(shareCode = "TRWN"),
            organisation = OrganisationFactory.school(
                id = OrganisationId(),
                name = "My school",
                address = Address(
                    state = State.fromCode("NY"),
                    country = Country.fromCode("USA")
                )
            )
        )

        saveUser(user)

        val userResource = userConverter.toUserResource(user)

        assertThat(userResource.teacherPlatformAttributes).isNotNull
        assertThat(userResource.teacherPlatformAttributes!!.shareCode).isEqualTo("TRWN")
    }

    @Test
    fun `converts a user without an organisation`() {
        val user = UserFactory.sample(
            organisation = null
        )
        saveUser(user)

        val userResource =
            userConverter.toUserResource(
                user = user
            )
        assertThat(userResource.organisation).isNull()
        assertThat(userResource.features).isNotNull
    }
}
