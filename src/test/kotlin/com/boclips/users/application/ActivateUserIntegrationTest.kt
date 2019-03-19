package com.boclips.users.application

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.presentation.SecurityContextUserNotFoundException
import com.boclips.users.presentation.UserActivationRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class ActivateUserIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var activateUser: ActivateUser

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
        setSecurityContext("user@example.com")

        val activatedUser = activateUser.activateUser(UserActivationRequest(referralCode = "it-is-a-referral"))

        val persistedUser = accountRepository.findById(activatedUser.id)
        assertThat(persistedUser!!.isReferral).isTrue()
    }

    @Test
    fun `activateUser when security context not populated throws`() {
        assertThrows<SecurityContextUserNotFoundException> {
            activateUser.activateUser(UserActivationRequest())
        }
    }
}
