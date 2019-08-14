package com.boclips.users.presentation.hateoas

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
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
                id = "lovely-user",
                activated = false
            )
        )

        val activateUserLink = userLinkBuilder.activateUserLink()

        assertThat(activateUserLink).isNotNull()
        assertThat(activateUserLink!!.href).endsWith("/users/lovely-user")
        assertThat(activateUserLink.rel).isEqualTo("activate")
    }

    @Test
    fun `activate link when authenticated and but not in the database`() {
        setSecurityContext("sso-first-time-user")

        val activateUserLink = userLinkBuilder.activateUserLink()

        assertThat(activateUserLink).isNotNull()
        assertThat(activateUserLink!!.href).endsWith("/users/sso-first-time-user")
        assertThat(activateUserLink.rel).isEqualTo("activate")
    }

    @Test
    fun `no activate link when authenticated and activated`() {
        setSecurityContext("lovely-user")

        userRepository.save(
            UserFactory.sample(
                id = "lovely-user",
                activated = true
            )
        )

        val activateUserLink = userLinkBuilder.activateUserLink()

        assertThat(activateUserLink).isNull()
    }

    @Test
    fun `no activate link when not authenticated`() {
        val activateUserLink = userLinkBuilder.activateUserLink()

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
    fun `getUserLink when authenticated`() {
        setSecurityContext("a-user")

        val getUserLink = userLinkBuilder.profileLink()

        assertThat(getUserLink).isNotNull()
        assertThat(getUserLink!!.href).endsWith("/users/a-user")
        assertThat(getUserLink.rel).isEqualTo("profile")
    }

    @Test
    fun `getUserLink when not authenticated`() {
        val getUserLink = userLinkBuilder.profileLink()

        assertThat(getUserLink).isNotNull()
        assertThat(getUserLink!!.href).endsWith("/users/{id}")
        assertThat(getUserLink.rel).isEqualTo("profile")
    }
}
