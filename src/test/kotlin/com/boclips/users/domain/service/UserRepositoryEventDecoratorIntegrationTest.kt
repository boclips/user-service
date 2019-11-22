package com.boclips.users.domain.service

import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class UserRepositoryEventDecoratorIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `it publishes an event when user is created`() {
        val organisation = organisationAccountRepository.save(OrganisationFactory.school())
        val user = userRepository.save(
            UserFactory.sample(
                organisationAccountId = organisation.id
            )
        )

        val event = eventBus.getEventOfType(UserCreated::class.java)
        assertThat(event.user.id).isEqualTo(user.id.value)
        assertThat(event.user.organisation.id).isEqualTo(organisation.id.value)
    }

    @Test
    fun `it publishes an event when user is updated`() {
        val maths = subjectService.addSubject(Subject(
            id = SubjectId(value = "1"),
            name = "Maths"
        ))
        val district = organisationAccountRepository.save(OrganisationFactory.district(name = "District 9"))
        val school = organisationAccountRepository.save(OrganisationFactory.school(
            name = "The Street Wise Academy",
            district = district,
            postCode = "012345"))
        val user = userRepository.save(UserFactory.sample(
            account = AccountFactory.sample(username = "dave@davidson.com"),
            profile = ProfileFactory.sample(firstName = "Dave", lastName = "Davidson", subjects = listOf(maths), ages = listOf(7)))
        )
        userRepository.update(user, UserUpdateCommand.ReplaceOrganisationId(school.id))

        val event = eventBus.getEventOfType(UserUpdated::class.java)
        assertThat(event.user.id).isEqualTo(user.id.value)
        assertThat(event.user.firstName).isEqualTo("Dave")
        assertThat(event.user.lastName).isEqualTo("Davidson")
        assertThat(event.user.email).isEqualTo("dave@davidson.com")
        assertThat(event.user.subjects).hasSize(1)
        assertThat(event.user.subjects.first().id.value).isEqualTo("1")
        assertThat(event.user.subjects.first().name).isEqualTo("Maths")
        assertThat(event.user.ages).hasSize(1)
        assertThat(event.user.ages.first()).isEqualTo(7)
        assertThat(event.user.organisation.id).isEqualTo(school.id.value)
        assertThat(event.user.organisation.accountType).isEqualTo("STANDARD")
        assertThat(event.user.organisation.name).isEqualTo("The Street Wise Academy")
        assertThat(event.user.organisation.parent.name).isEqualTo("District 9")
        assertThat(event.user.organisation.postcode).isEqualTo("012345")
    }
}
