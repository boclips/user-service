package com.boclips.users.domain.service.events

import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.service.UserUpdate
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserRepositoryEventDecoratorIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `it publishes an event when user is created`() {
        val organisation =
            organisationRepository.save(organisation = OrganisationFactory.sample(details = OrganisationDetailsFactory.school()))
        val user = userRepository.create(
            UserFactory.sample(
                organisation = organisation
            )
        )

        val event = eventBus.getEventOfType(UserCreated::class.java)
        assertThat(event.user.id).isEqualTo(user.id.value)
        assertThat(event.user.organisation.id).isEqualTo(organisation.id.value)
    }

    @Test
    fun `it publishes an event when user is updated`() {
        val maths = saveSubject("Maths")

        val district = organisationRepository.save(
            organisation = OrganisationFactory.sample(
                details = OrganisationDetailsFactory.district(name = "District 9")
            )
        )
        val school = organisationRepository.save(
            organisation = OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "The Street Wise Academy",
                    district = district,
                    postCode = "012345"
                )
            )
        )
        val user = userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(username = "dave@davidson.com"),
                profile = ProfileFactory.sample(
                    firstName = "Dave",
                    lastName = "Davidson",
                    subjects = listOf(maths),
                    ages = listOf(7)
                )
            )
        )
        userRepository.update(
            user,
            UserUpdate.ReplaceOrganisation(school)
        )

        val event = eventBus.getEventOfType(UserUpdated::class.java)
        assertThat(event.user.id).isEqualTo(user.id.value)
        assertThat(event.user.profile.firstName).isEqualTo("Dave")
        assertThat(event.user.profile.lastName).isEqualTo("Davidson")
        assertThat(event.user.email).isEqualTo("dave@davidson.com")
        assertThat(event.user.profile.subjects.first().id.value).isEqualTo(maths.id.value)
        assertThat(event.user.profile.subjects.first().name).isEqualTo(maths.name)
        assertThat(event.user.profile.ages).hasSize(1)
        assertThat(event.user.profile.ages.first()).isEqualTo(7)
        assertThat(event.user.organisation.id).isEqualTo(school.id.value)
        assertThat(event.user.organisation.accountType).isEqualTo("STANDARD")
        assertThat(event.user.organisation.name).isEqualTo("The Street Wise Academy")
        assertThat(event.user.organisation.parent.name).isEqualTo("District 9")
        assertThat(event.user.organisation.postcode).isEqualTo("012345")
        assertThat(event.user.organisation.countryCode).isEqualTo("USA")
        assertThat(event.user.organisation.state).isEqualTo("IL")
    }
}
