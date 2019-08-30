package com.boclips.users.domain.service

import com.boclips.eventbus.events.user.UserCreated
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EventPublishingUserRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `it publishes a user created event given a User`() {
        val organisation = OrganisationFactory.sample(id = OrganisationId("quite-something"))
        val user = userRepository.save(
            UserFactory.sample(
                organisationId = organisation.id
            )
        )

        val event = eventBus.getEventOfType(UserCreated::class.java)
        assertThat(event.user.id).isEqualTo(user.id.value)
        assertThat(event.user.organisationId).isEqualTo("quite-something")
    }
}
