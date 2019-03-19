package com.boclips.users.application

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.presentation.SecurityContextUserNotFoundException
import com.boclips.users.presentation.UserActivationRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.UserFactory
import com.boclips.users.testsupport.UserIdentityFactory
import com.nhaarman.mockitokotlin2.check
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class ActivateUserIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var activateUser: ActivateUser

    @Autowired
    lateinit var referralProvider: ReferralProvider

    @Test
    fun `activates new user if user does not exist`() {
        setSecurityContext("user@example.com")

        val activatedUser = activateUser.activateUser(UserActivationRequest())

        val persistedUser = accountRepository.findById(activatedUser.id)
        assertThat(persistedUser).isNotNull
        assertThat(persistedUser!!.activated).isTrue()
    }

    @Test
    fun `activate user is idempotent`() {
        setSecurityContext("user@example.com")

        assertThat(activateUser.activateUser(UserActivationRequest()))
            .isEqualTo(
                activateUser.activateUser(UserActivationRequest())
            )
    }

    @Test
    fun `activate user does not mark as referred if referral code is null`() {
        setSecurityContext("user@example.com")

        val activatedUser = activateUser.activateUser(UserActivationRequest())

        val persistedUser = accountRepository.findById(activatedUser.id)
        assertThat(persistedUser!!.isReferral).isFalse()
    }

    @Test
    fun `activate user tracks whether it's a referral or not`() {
        val identity = UUID.randomUUID().toString()

        setSecurityContext(identity)

        saveUser(
            UserFactory.sample(
                userId = UserId(value = identity),
                identity = UserIdentityFactory.sample(
                    id = identity,
                    firstName = "Jane",
                    lastName = "Doe",
                    email = "jane@doe.com",
                    isVerified = true
                )
            )
        )

        val activatedUser = activateUser.activateUser(UserActivationRequest(referralCode = "it-is-a-referral"))

        val persistedAccount = accountRepository.findById(activatedUser.id)
        assertThat(persistedAccount!!.isReferral).isTrue()

        verify(referralProvider).createReferral(check {
            assertThat(it.referralCode).isEqualTo("it-is-a-referral")
            assertThat(it.firstName).isEqualTo("Jane")
            assertThat(it.lastName).isEqualTo("Doe")
            assertThat(it.email).isEqualTo("jane@doe.com")
            assertThat(it.status).isEqualTo("qualified")
            assertThat(it.externalIdentifier).isEqualTo(identity)
        })
    }

    @Test
    fun `activateUser when security context not populated throws`() {
        assertThrows<SecurityContextUserNotFoundException> {
            activateUser.activateUser(UserActivationRequest())
        }
    }
}
