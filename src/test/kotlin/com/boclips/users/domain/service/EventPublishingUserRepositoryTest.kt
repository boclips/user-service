package com.boclips.users.domain.service

import com.boclips.eventbus.events.user.UserCreated
import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EventPublishingUserRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `it publishes an user created event api customers given a User`() {
        val user = userRepository.save(
            UserFactory.sample(
                account = AccountFactory.sample(organisationType = OrganisationType.ApiCustomer(OrganisationId("quite-something")))
            )
        )

        val event = eventBus.getEventOfType(UserCreated::class.java)
        assertThat(event.user.id).isEqualTo(user.id.value)
        assertThat(event.user.organisationId).isEqualTo("quite-something")
    }

    @Test
    fun `it publishes a user event for Boclips For Teachers given a User`() {
        val user = userRepository.save(UserFactory.sample())

        val event = eventBus.getEventOfType(UserCreated::class.java)
        assertThat(event.user.id).isEqualTo(user.id.value)
        assertThat(event.user.organisationId).isEqualTo(null)
    }

    @Test
    fun `it publishes a user event for api customers given an Account`() {
        val user =
            userRepository.save(AccountFactory.sample(organisationType = OrganisationType.ApiCustomer(OrganisationId("quite-something"))))

        val event = eventBus.getEventOfType(UserCreated::class.java)
        assertThat(event.user.id).isEqualTo(user.id.value)
        assertThat(event.user.organisationId).isEqualTo("quite-something")
    }

    @Test
    fun `it publishes a user event for Boclips For Teachers given an Account`() {
        val user =
            userRepository.save(AccountFactory.sample())

        val event = eventBus.getEventOfType(UserCreated::class.java)
        assertThat(event.user.id).isEqualTo(user.id.value)
        assertThat(event.user.organisationId).isEqualTo(null)
    }
}
