package com.boclips.users.application

import com.boclips.eventbus.events.user.UserActivated
import com.boclips.security.testing.setSecurityContext
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.AccountFactory
import com.boclips.users.testsupport.OrganisationIdFactory
import com.boclips.users.testsupport.UserFactory
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
        val identity = UUID.randomUUID().toString()
        setSecurityContext(identity)

        saveUser(UserFactory.sample(user = AccountFactory.sample(id = identity)))

        assertThat(activateUser()).isEqualTo(activateUser())
    }

    @Test
    fun `activate user publishes an event`() {
        val identity1 = UUID.randomUUID().toString()
        setSecurityContext(identity1)
        saveUser(UserFactory.sample(user = AccountFactory.sample(id = identity1)))

        val identity2 = UUID.randomUUID().toString()
        setSecurityContext(identity2)
        saveUser(UserFactory.sample(user = AccountFactory.sample(id = identity2)))

        activateUser()

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
                user = AccountFactory.sample(
                    id = userId,
                    organisationId = null
                )
            )
        )

        activateUser()

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
                user = AccountFactory.sample(
                    id = userId,
                    organisationId = OrganisationIdFactory.sample(id = organisationId)
                )
            )
        )

        activateUser()

        val event = eventBus.getEventOfType(UserActivated::class.java)

        assertThat(event.user.organisationId).isEqualTo(organisationId)
    }

    @Test
    fun `activate user marks referral`() {
        val identity = UUID.randomUUID().toString()
        setSecurityContext(identity)

        saveUser(
            UserFactory.sample(
                user = AccountFactory.sample(
                    id = identity,
                    referralCode = "it-is-a-referral",
                    firstName = "Jane",
                    lastName = "Doe",
                    email = "jane@doe.com"
                )
            )
        )

        activateUser()

        verify(referralProvider).createReferral(com.nhaarman.mockitokotlin2.check {
            Assertions.assertThat(it.referralCode).isEqualTo("it-is-a-referral")
            Assertions.assertThat(it.firstName).isEqualTo("Jane")
            Assertions.assertThat(it.lastName).isEqualTo("Doe")
            Assertions.assertThat(it.email).isEqualTo("jane@doe.com")
            Assertions.assertThat(it.status).isEqualTo("qualified")
            Assertions.assertThat(it.externalIdentifier).isEqualTo(identity)
        })
    }

    @Test
    fun `cannot activate user when security context not populated`() {
        assertThrows<NotAuthenticatedException> {
            activateUser()
        }
    }
}
