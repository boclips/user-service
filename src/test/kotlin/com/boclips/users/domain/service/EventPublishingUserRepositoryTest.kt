package com.boclips.users.domain.service

import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EventPublishingUserRepositoryTest : AbstractSpringIntegrationTest() {

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
        assertThat(event.organisation.id).isEqualTo(organisation.id.value)
    }

    @Test
    fun `it publishes an event when user is updated`() {
        val organisation = organisationAccountRepository.save(OrganisationFactory.school())
        val user = userRepository.save(UserFactory.sample())
        userRepository.update(user, UserUpdateCommand.ReplaceOrganisationId(organisation.id))

        val event = eventBus.getEventOfType(UserUpdated::class.java)
        assertThat(event.user.id).isEqualTo(user.id.value)
        assertThat(event.organisation.id).isEqualTo(organisation.id.value)
    }
}
