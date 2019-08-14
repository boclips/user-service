package com.boclips.users.application

import com.boclips.eventbus.events.user.UserActivated
import com.boclips.security.testing.setSecurityContext
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.domain.model.UserNotFoundException
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationIdFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class ActivateUserIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var activateUser: ActivateUser

    @Test
    fun `activate user is idempotent`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        saveUser(UserFactory.sample(id = userId))

        assertThat(activateUser(userId)).isEqualTo(activateUser(userId))
    }

    @Test
    fun `activate user publishes an event`() {
        val userId1 = UUID.randomUUID().toString()
        setSecurityContext(userId1)
        saveUser(UserFactory.sample(id = userId1))

        val userId2 = UUID.randomUUID().toString()
        setSecurityContext(userId2)
        saveUser(UserFactory.sample(id = userId2))

        activateUser(userId2)

        val event = eventBus.getEventOfType(UserActivated::class.java)

        assertThat(event.totalUsers).isEqualTo(2)
        assertThat(event.activatedUsers).isEqualTo(1)
    }

    @Test
    fun `activate user does not pass organisation to an event if given user does not belong to one`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)
        saveUser(
            UserFactory.sample(
                id = userId,
                associatedTo = null
            )
        )

        activateUser(userId)

        val event = eventBus.getEventOfType(UserActivated::class.java)

        assertThat(event.user.organisationId).isNull()
    }

    @Test
    fun `activate user passes organisation to an event if given user belongs to organisation`() {
        val userId = UUID.randomUUID().toString()
        val organisationId = UUID.randomUUID().toString()
        setSecurityContext(userId)
        saveUser(
            UserFactory.sample(
                id = userId,
                associatedTo = OrganisationIdFactory.sample(id = organisationId)
            )
        )

        activateUser(userId)

        val event = eventBus.getEventOfType(UserActivated::class.java)

        assertThat(event.user.organisationId).isEqualTo(organisationId)
    }

    @Test
    fun `activate user marks referral`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        saveUser(
            UserFactory.sample(
                id = userId,
                referralCode = "it-is-a-referral",
                firstName = "Jane",
                lastName = "Doe",
                email = "jane@doe.com"
            )
        )

        activateUser(userId)

        verify(referralProvider).createReferral(com.nhaarman.mockitokotlin2.check {
            Assertions.assertThat(it.referralCode).isEqualTo("it-is-a-referral")
            Assertions.assertThat(it.firstName).isEqualTo("Jane")
            Assertions.assertThat(it.lastName).isEqualTo("Doe")
            Assertions.assertThat(it.email).isEqualTo("jane@doe.com")
            Assertions.assertThat(it.status).isEqualTo("qualified")
            Assertions.assertThat(it.externalIdentifier).isEqualTo(userId)
        })
    }

    @Test
    fun `cannot activate user when security context not populated`() {
        assertThrows<NotAuthenticatedException> {
            activateUser("peanuts")
        }
    }

    @Test
    fun `cannot obtain user information of another user`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext("different-user")

        saveUser(UserFactory.sample())

        assertThrows<PermissionDeniedException> { activateUser(userId) }
    }

    @Test
    fun `get user throws when user doesn't exist anywhere`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        assertThrows<UserNotFoundException> { activateUser(userId) }
    }
}
