package com.boclips.users.application

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.presentation.SecurityContextUserNotFoundException
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

        val activatedUser = activateUser.activateUser()

        val persistedUser = accountRepository.findById(activatedUser.id)
        assertThat(persistedUser).isNotNull
        assertThat(persistedUser!!.activated).isTrue()
    }

    @Test
    fun `activate user is idempotent`() {
        setSecurityContext("user@example.com")

        assertThat(activateUser.activateUser()).isEqualTo(activateUser.activateUser())
    }

    @Test
    fun `activateUser when security context not populated throws`() {
        assertThrows<SecurityContextUserNotFoundException> {
            activateUser.activateUser()
        }
    }
}
