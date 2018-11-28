package com.boclips.users.application

import com.boclips.users.domain.model.UserRepository
import com.boclips.users.presentation.SecurityContextUserNotFoundException
import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import com.boclips.users.testsupport.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

class UserActionsIntegrationTest : AbstractSpringIntergrationTest() {

    @Autowired
    lateinit var userActions: UserActions

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `activateUser when security context not populated throws`() {
        assertThrows<SecurityContextUserNotFoundException> {
            userActions.activateUser()
        }
    }

    @Test
    fun `getLinks when security context not populated returns no links`() {
        assertThat(userActions.getLinks().links).isEmpty()
    }

    @Test
    @WithMockUser("user-id")
    fun `getLinks when active user returns profile link only`() {
        userRepository.save(UserFactory.sample(id = "user-id", activated = true))
        assertThat(userActions.getLinks().links.map { it.rel }).containsExactly("profile")
    }

    @Test
    @WithMockUser("user-id")
    fun `getLinks when non existing user returns activate link only`() {
        assertThat(userActions.getLinks().links.map { it.rel }).containsExactly("activate")
    }

    @Test
    @WithMockUser("user-id")
    fun `getLinks when non activated user returns activate link only`() {
        userRepository.save(UserFactory.sample(id = "user-id", activated = false))
        assertThat(userActions.getLinks().links.map { it.rel }).containsExactly("activate")
    }
}