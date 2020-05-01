package com.boclips.users.domain.service.events

import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.domain.model.subject.Subject
import com.boclips.users.domain.model.subject.SubjectId
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.domain.model.user.UserUpdate
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import com.boclips.eventbus.domain.Subject as EventSubject
import com.boclips.eventbus.domain.SubjectId as EventSubjectId

class EventConverterTest : AbstractSpringIntegrationTest() {

    @Test
    fun `converting user to event`() {
        val user = UserFactory.sample(
            identity = IdentityFactory.sample(
                createdAt = ZonedDateTime.parse("2020-03-20T10:11:12Z")
            ),
            profile = ProfileFactory.sample(
                firstName = "John",
                lastName = "Johnson",
                subjects = listOf(
                    Subject(
                        id = SubjectId(
                            "subject-id"
                        ), name = "maths"
                    )
                ),
                ages = listOf(5, 6, 7, 8),
                school = OrganisationFactory.school(name = "School name")
            )
        )

        val eventUser = EventConverter().toEventUser(user)

        assertThat(eventUser.createdAt).isEqualTo("2020-03-20T10:11:12Z")
        assertThat(eventUser.profile.firstName).isEqualTo("John")
        assertThat(eventUser.profile.lastName).isEqualTo("Johnson")
        assertThat(eventUser.profile.subjects).containsExactly(EventSubject(EventSubjectId("subject-id"), "maths"))
        assertThat(eventUser.profile.ages).containsExactly(5, 6, 7, 8)
        assertThat(eventUser.profile.school?.name).isEqualTo("School name")
    }

    @Test
    fun `when user is assigned only to a district, the parent is set null`() {
        val district = organisationRepository.save(
            organisation = OrganisationFactory.district(name = "District 9")
        )

        val user = userRepository.create(UserFactory.sample())

        userRepository.update(user, UserUpdate.ReplaceOrganisation(district))

        val event = eventBus.getEventOfType(UserUpdated::class.java)
        assertThat(event.user.id).isEqualTo(user.id.value)
        assertThat(event.user.organisation.id).isEqualTo(district.id.value)
        assertThat(event.user.organisation.type).isEqualTo("DISTRICT")
        assertThat(event.user.organisation.accountType).isEqualTo("STANDARD")
        assertThat(event.user.organisation.name).isEqualTo("District 9")
        assertThat(event.user.organisation.parent).isNull()
    }

    @Test
    fun `convert role information if exists`() {
        val user = userRepository.create(UserFactory.sample(profile = ProfileFactory.sample(role = null)))

        userRepository.update(user, UserUpdate.ReplaceRole("PARENT"))

        val event = eventBus.getEventOfType(UserUpdated::class.java)
        assertThat(event.user.id).isEqualTo(user.id.value)
        assertThat(event.user.profile.role).isEqualTo("PARENT")
    }

    @Test
    fun `convert country code and state if exists`() {
        val school = organisationRepository.save(
            organisation = OrganisationFactory.school(
                address = Address(
                    country = Country.fromCode("USA"),
                    state = State.fromCode("IL")
                )
            )
        )

        val user = userRepository.create(UserFactory.sample(organisation = null))

        userRepository.update(user, UserUpdate.ReplaceOrganisation(school))

        val event = eventBus.getEventOfType(UserUpdated::class.java)
        assertThat(event.user.id).isEqualTo(user.id.value)
        assertThat(event.user.organisation.countryCode).isEqualTo("USA")
        assertThat(event.user.organisation.state).isEqualTo("IL")
    }
}
