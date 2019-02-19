package com.boclips.users.application

import com.boclips.users.domain.model.User
import com.boclips.users.presentation.SecurityContextUserNotFoundException
import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import com.boclips.users.testsupport.AccountFactory
import com.boclips.users.testsupport.UserFactory
import com.boclips.users.testsupport.UserIdentityFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

class AccountActionsIntegrationTest : AbstractSpringIntergrationTest() {

    @Autowired
    lateinit var userActions: UserActions

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
        saveUser(createUser(true))

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
        saveUser(createUser(false))

        assertThat(userActions.getLinks().links.map { it.rel }).containsExactly("activate")
    }

    private fun createUser(isActivated: Boolean): User {
        return UserFactory.sample(
            account = AccountFactory.sample(id = "user-id", activated = isActivated),
            identity = UserIdentityFactory.sample(id = "user-id")
        )
    }
}
