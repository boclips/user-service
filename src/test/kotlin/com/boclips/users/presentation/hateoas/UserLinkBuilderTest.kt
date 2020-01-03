package com.boclips.users.presentation.hateoas

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import java.time.ZonedDateTime

class UserLinkBuilderTest : AbstractSpringIntegrationTest() {

    private lateinit var userLinkBuilder: UserLinkBuilder

    @AfterEach
    fun cleanUp() {
        SecurityContextHolder.clearContext()
    }

    @BeforeEach
    fun setUp() {
        userLinkBuilder = UserLinkBuilder(userRepository = userRepository, accessService = accessService)
    }

    @Test
    fun `activate link when authenticated and not yet activated`() {
        setSecurityContext("lovely-user")

        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = "lovely-user"),
                profile = null,
                organisationAccountId = null
            )
        )

        val activateUserLink = userLinkBuilder.activateUserLink()

        assertThat(activateUserLink).isNotNull()
        assertThat(activateUserLink!!.href).endsWith("/users/lovely-user")
        assertThat(activateUserLink.rel).isEqualTo("activate")
    }

    @Test
    fun `activate link when authenticated, has partial profile information but has not onboarded`() {
        setSecurityContext("lovely-user")

        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = "lovely-user"),
                profile = ProfileFactory.sample(firstName = ""),
                organisationAccountId = null
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

        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = "lovely-user"),
                profile = ProfileFactory.sample(),
                organisationAccountId = AccountId("test")
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

    @Test
    fun `contracts link when authenticated and has VIEW_CONTRACTS role`() {
        setSecurityContext("a-user", UserRoles.VIEW_CONTRACTS)

        val contractsLink = userLinkBuilder.contractsLink(UserId("a-user"))

        assertThat(contractsLink).isNotNull()
        assertThat(contractsLink!!.href).endsWith("/users/a-user/contracts")
        assertThat(contractsLink.rel).isEqualTo("contracts")
    }

    @Test
    fun `no contracts link when does not have VIEW_CONTRACTS role`() {
        setSecurityContext("a-user")

        val contractsLink = userLinkBuilder.contractsLink(UserId("a-user"))

        assertThat(contractsLink).isNull()
    }

    @Test
    fun `no reportAccessExpired link when user has access`() {
        setSecurityContext("lovely-user")

        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = "lovely-user"),
                profile = ProfileFactory.sample(),
                organisationAccountId = AccountId("test"),
                accessExpiresOn = null
            )
        )

        val reportAccessExpiredLink = userLinkBuilder.reportAccessExpiredLink()

        assertThat(reportAccessExpiredLink).isNull()
    }

    @Test
    fun `no reportAccessExpired link when user has expired`() {
        setSecurityContext("lovely-user")

        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = "lovely-user"),
                profile = ProfileFactory.sample(),
                organisationAccountId = AccountId("test"),
                accessExpiresOn = ZonedDateTime.now().minusDays(1)
            )
        )

        val reportAccessExpiredLink = userLinkBuilder.reportAccessExpiredLink()

        assertThat(reportAccessExpiredLink).isNotNull
        assertThat(reportAccessExpiredLink!!.href).endsWith("/events/expired-user-access")
    }

    @Test
    fun `no reportAccessExpired link when unauthenticated`() {
        val reportAccessExpiredLink = userLinkBuilder.reportAccessExpiredLink()

        assertThat(reportAccessExpiredLink).isNull()
    }
}
