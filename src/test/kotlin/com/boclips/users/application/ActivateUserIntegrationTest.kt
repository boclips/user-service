package com.boclips.users.application

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.AccountFactory
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

        val message = messageCollector.forChannel(topics.userActivated()).poll()

        assertThat(message.toString()).contains("\"totalUsers\":2")
        assertThat(message.toString()).contains("\"activatedUsers\":1")
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
