package com.boclips.users.domain.service.events

import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.domain.service.UserUpdate
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions

import org.junit.jupiter.api.Test

internal class EventConverterTest : AbstractSpringIntegrationTest() {
    @Test
    fun `when user is assigned only to a district, the parent is set null`() {
        val district = organisationRepository.save(
            organisation = OrganisationFactory.sample(
                details = OrganisationDetailsFactory.district(name = "District 9")
            )
        )

        val user = userRepository.create(UserFactory.sample())

        userRepository.update(user, UserUpdate.ReplaceOrganisation(district))

        val event = eventBus.getEventOfType(UserUpdated::class.java)
        Assertions.assertThat(event.user.id).isEqualTo(user.id.value)
        Assertions.assertThat(event.user.organisation.id).isEqualTo(district.id.value)
        Assertions.assertThat(event.user.organisation.type).isEqualTo("DISTRICT")
        Assertions.assertThat(event.user.organisation.accountType).isEqualTo("STANDARD")
        Assertions.assertThat(event.user.organisation.name).isEqualTo("District 9")
        Assertions.assertThat(event.user.organisation.parent).isNull()
    }

    @Test
    fun `convert role information if exists`() {
        val user = userRepository.create(UserFactory.sample(profile = ProfileFactory.sample(role = null)))

        userRepository.update(user, UserUpdate.ReplaceRole("PARENT"))

        val event = eventBus.getEventOfType(UserUpdated::class.java)
        Assertions.assertThat(event.user.id).isEqualTo(user.id.value)
        Assertions.assertThat(event.user.role).isEqualTo("PARENT")
    }
}
