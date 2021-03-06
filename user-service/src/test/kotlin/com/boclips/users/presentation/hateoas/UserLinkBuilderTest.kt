package com.boclips.users.presentation.hateoas

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
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
        userLinkBuilder = UserLinkBuilder()
    }

    @Test
    fun `activate link when authenticated and not yet activated`() {
        setSecurityContext("lovely-user")

        val activateUserLink =
            userLinkBuilder.activateUserLink(
                UserFactory.sample(
                    profile = null,
                    identity = IdentityFactory.sample(id = "lovely-user")
                )
            )

        assertThat(activateUserLink).isNotNull()
        assertThat(activateUserLink!!.href).endsWith("/users/lovely-user")
        assertThat(activateUserLink.rel.value()).isEqualTo("activate")
    }

    @Test
    fun `no activate link when USER_DATA_HIDDEN feature set to true`() {
        setSecurityContext("lovely-user")

        val activateUserLink =
            userLinkBuilder.activateUserLink(
                UserFactory.sample(
                    profile = null,
                    identity = IdentityFactory.sample(id = "lovely-user"),
                    organisation = OrganisationFactory.district(
                        features = mapOf(Feature.USER_DATA_HIDDEN to true)
                    )
                )
            )

        assertThat(activateUserLink).isNull()
    }

    @Test
    fun `activate link when authenticated, has partial profile information but has not onboarded`() {
        setSecurityContext("lovely-user")

        val activateUserLink =
            userLinkBuilder.activateUserLink(
                UserFactory.sample(
                    identity = IdentityFactory.sample(id = "lovely-user"),
                    profile = ProfileFactory.sample(firstName = "")
                )
            )

        assertThat(activateUserLink).isNotNull()
        assertThat(activateUserLink!!.href).endsWith("/users/lovely-user")
        assertThat(activateUserLink.rel.value()).isEqualTo("activate")
    }

    @Test
    fun `activate link when authenticated and but not in the database`() {
        setSecurityContext("sso-first-time-user")

        val activateUserLink = userLinkBuilder.activateUserLink(null)

        assertThat(activateUserLink).isNotNull()
        assertThat(activateUserLink!!.href).endsWith("/users/sso-first-time-user")
        assertThat(activateUserLink.rel.value()).isEqualTo("activate")
    }

    @Test
    fun `no activate link when authenticated and activated`() {
        setSecurityContext("lovely-user")

        val activateUserLink =
            userLinkBuilder.activateUserLink(
                UserFactory.sample(
                    identity = IdentityFactory.sample(id = "lovely-user"),
                    profile = ProfileFactory.sample()
                )
            )

        assertThat(activateUserLink).isNull()
    }

    @Test
    fun `no activate link when not authenticated`() {
        val activateUserLink = userLinkBuilder.activateUserLink(null)

        assertThat(activateUserLink).isNull()
    }

    @Test
    fun `createAccount link when not authenticated`() {
        val createUserLink = userLinkBuilder.createUserLink()

        assertThat(createUserLink).isNotNull()
        assertThat(createUserLink!!.href).endsWith("/users")
        assertThat(createUserLink.rel.value()).isEqualTo("createAccount")
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
        assertThat(profileLink.rel.value()).isEqualTo("profile")
    }

    @Test
    fun `profile link when not authenticated`() {
        val profileLink = userLinkBuilder.profileLink()

        assertThat(profileLink).isNull()
    }

    @Test
    fun `profile link when overridden`() {
        setSecurityContext("a-user")

        val profileLink = userLinkBuilder.profileLink(UserId("different-user"))

        assertThat(profileLink).isNotNull
        assertThat(profileLink!!.href).endsWith("/users/different-user")
        assertThat(profileLink.rel.value()).isEqualTo("profile")
    }

    @Test
    fun `profile link for new user when not authenticated`() {
        val userId = "new-user-id"
        val profileLink = userLinkBuilder.newUserProfileLink(
            UserId(
                userId
            )
        )

        assertThat(profileLink).isNotNull
        assertThat(profileLink!!.href).endsWith("/users/$userId")
        assertThat(profileLink.rel.value()).isEqualTo("profile")
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
        assertThat(userLink.rel.value()).isEqualTo("user")
    }

    @Test
    fun `access rules link when authenticated and has VIEW_ACCESS_RULES role`() {
        setSecurityContext("a-user", UserRoles.VIEW_ACCESS_RULES)

        val accessRulesLink = userLinkBuilder.accessRulesLink(
            UserId(
                "a-user"
            )
        )

        assertThat(accessRulesLink).isNotNull()
        assertThat(accessRulesLink!!.href).endsWith("/users/a-user/access-rules{?client}")
        assertThat(accessRulesLink.rel.value()).isEqualTo("accessRules")
    }

    @Test
    fun `no access rules link when does not have VIEW_ACCESS_RULES role`() {
        setSecurityContext("a-user")

        val accessRulesLink = userLinkBuilder.accessRulesLink(
            UserId(
                "a-user"
            )
        )

        assertThat(accessRulesLink).isNull()
    }

    @Test
    fun `no reportAccessExpired link when user has access`() {
        setSecurityContext("lovely-user")

        val user = UserFactory.sample(
            identity = IdentityFactory.sample(id = "lovely-user"),
            profile = ProfileFactory.sample(),
            accessExpiresOn = null
        )

        val reportAccessExpiredLink = userLinkBuilder.reportAccessExpiredLink(user = user, hasAccess = true)

        assertThat(reportAccessExpiredLink).isNull()
    }

    @Test
    fun `no reportAccessExpired link when user has expired`() {
        setSecurityContext("lovely-user")

        val user = UserFactory.sample(
            identity = IdentityFactory.sample(id = "lovely-user"),
            profile = ProfileFactory.sample()
        )

        val reportAccessExpiredLink = userLinkBuilder.reportAccessExpiredLink(user, false)

        assertThat(reportAccessExpiredLink).isNotNull
        assertThat(reportAccessExpiredLink!!.href).endsWith("/events/expired-user-access")
    }

    @Test
    fun `no reportAccessExpired link when unauthenticated`() {
        val user = UserFactory.sample(
            identity = IdentityFactory.sample(id = "lovely-user"),
            profile = ProfileFactory.sample()
        )

        val reportAccessExpiredLink = userLinkBuilder.reportAccessExpiredLink(user, false)

        assertThat(reportAccessExpiredLink).isNull()
    }

    @Test
    fun `validateShareCode link`() {
        val validateShareCodeLink = userLinkBuilder.validateShareCodeLink()

        assertThat(validateShareCodeLink).isNotNull
        assertThat(validateShareCodeLink!!.href).endsWith("/users/{id}/shareCode/{shareCode}")
    }

    @Test
    fun `isUserActive link`() {
        val isUserActive = userLinkBuilder.isUserActiveLink()

        assertThat(isUserActive).isNotNull
        assertThat(isUserActive!!.href).endsWith("/users/{id}/active")
    }
}
