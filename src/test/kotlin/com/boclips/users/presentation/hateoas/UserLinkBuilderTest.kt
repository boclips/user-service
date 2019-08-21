package com.boclips.users.presentation.hateoas

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder

class UserLinkBuilderTest : AbstractSpringIntegrationTest() {

    private lateinit var userLinkBuilder: UserLinkBuilder

    @AfterEach
    fun cleanUp() {
        SecurityContextHolder.clearContext()
    }

    @BeforeEach
    fun setUp() {
        userLinkBuilder = UserLinkBuilder(userRepository = userRepository)
    }

    @Test
    fun `activate link when authenticated and not yet activated`() {
        setSecurityContext("lovely-user")

        userRepository.save(
            UserFactory.sample(
                account = AccountFactory.sample(id = "lovely-user"),
                profile = null
            )
        )

        val activateUserLink = userLinkBuilder.updateUserLink()

        assertThat(activateUserLink).isNotNull()
        assertThat(activateUserLink!!.href).endsWith("/users/lovely-user")
        assertThat(activateUserLink.rel).isEqualTo("activate")
    }

    @Test
    fun `activate link when authenticated and but not in the database`() {
        setSecurityContext("sso-first-time-user")

        val activateUserLink = userLinkBuilder.updateUserLink()

        assertThat(activateUserLink).isNotNull()
        assertThat(activateUserLink!!.href).endsWith("/users/sso-first-time-user")
        assertThat(activateUserLink.rel).isEqualTo("activate")
    }

    @Test
    fun `no activate link when authenticated and activated`() {
        setSecurityContext("lovely-user")

        userRepository.save(
            UserFactory.sample(
                account = AccountFactory.sample(id = "lovely-user"),
                profile = ProfileFactory.sample()
            )
        )

        val activateUserLink = userLinkBuilder.updateUserLink()

        assertThat(activateUserLink).isNull()
    }

    @Test
    fun `no activate link when not authenticated`() {
        val activateUserLink = userLinkBuilder.updateUserLink()

        assertThat(activateUserLink).isNull()
    }

    @Test
    fun `createAccount link when not authenticated`() {
        val createUserLink = userLinkBuilder.createUserLink()

        assertThat(createUserLink).isNotNull()
        assertThat(createUserLink!!.href).endsWith("/users")
        assertThat(createUserLink.rel).isEqualTo("createAccount")
    }

    @Test
    fun `no createAccount link when authenticated`() {
        setSecurityContext("a-user")

        val createUserLink = userLinkBuilder.createUserLink()

        assertThat(createUserLink).isNull()
    }

    @Test
    fun `profile link when authenticated`() {
        setSecurityContext("a-user")

        val profileLink = userLinkBuilder.profileLink()

        assertThat(profileLink).isNotNull
        assertThat(profileLink!!.href).endsWith("/users/a-user")
        assertThat(profileLink.rel).isEqualTo("profile")
    }

    @Test
    fun `profile link when not authenticated`() {
        val profileLink = userLinkBuilder.profileLink()

        assertThat(profileLink).isNull()
    }

    @Test
    fun `profile link for new user when not authenticated`() {
        val userId = "new-user-id"
        val profileLink = userLinkBuilder.newUserProfileLink(UserId(userId))

        assertThat(profileLink).isNotNull
        assertThat(profileLink!!.href).endsWith("/users/$userId")
        assertThat(profileLink.rel).isEqualTo("profile")
    }

    @Test
    fun `user link when not authenticated`() {
        val userLink = userLinkBuilder.userLink()

        assertThat(userLink).isNull()
    }

    @Test
    fun `user link when authenticated without VIEW_USERS role`() {
        setSecurityContext("a-user")

        val userLink = userLinkBuilder.userLink()

        assertThat(userLink).isNull()
    }

    @Test
    fun `user link when authenticated and has VIEW_USERS role`() {
        setSecurityContext("a-user", UserRoles.VIEW_USERS)

        val userLink = userLinkBuilder.userLink()

        assertThat(userLink).isNotNull()
        assertThat(userLink!!.href).endsWith("/users/{id}")
        assertThat(userLink.rel).isEqualTo("user")
    }
}
