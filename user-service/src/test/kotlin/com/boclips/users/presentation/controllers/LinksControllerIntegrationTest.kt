package com.boclips.users.presentation.controllers

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.startsWith
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.ZonedDateTime

class LinksControllerIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `anonymous user`() {
        mvc.perform(get("/v1/"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.activate").doesNotExist())
            .andExpect(jsonPath("$._links.user").doesNotExist())
            .andExpect(jsonPath("$._links.profile").doesNotExist())
            .andExpect(jsonPath("$._links.createAccount").exists())
            .andExpect(jsonPath("$._links.accessRules").doesNotExist())
            .andExpect(jsonPath("$._links.countries").doesNotExist())
            .andExpect(jsonPath("$._links.searchAccessRules").doesNotExist())
            .andExpect(jsonPath("$._links.trackPageRendered").exists())
            .andExpect(jsonPath("$._links.trackPlatformInteractedWith").exists())
            .andExpect(jsonPath("$._links.organisations").doesNotExist())
            .andExpect(jsonPath("$._links.validateShareCode").exists())
    }

    @Test
    fun `registered user`() {
        setSecurityContext("a-user-id")

        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = "a-user-id"),
                profile = null
            )
        )

        mvc.perform(get("/v1/").asUser("a-user-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.profile").exists())
            .andExpect(jsonPath("$._links.countries").exists())
            .andExpect(jsonPath("$._links.activate.href", endsWith("/users/a-user-id")))
            .andExpect(jsonPath("$._links.createAccount").doesNotExist())
            .andExpect(jsonPath("$._links.accessRules").doesNotExist())
            .andExpect(jsonPath("$._links.searchAccessRules").doesNotExist())
            .andExpect(jsonPath("$._links.trackPageRendered").exists())
            .andExpect(jsonPath("$._links.trackPlatformInteractedWith").exists())
            .andExpect(jsonPath("$._links.organisations").doesNotExist())
            .andExpect(jsonPath("$._links.validateShareCode").exists())
    }

    @Test
    fun `registered user with no firstName (has not onboarded)`() {
        setSecurityContext("a-user-id")

        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = "a-user-id"),
                profile = ProfileFactory.sample(firstName = ""),
                organisation = null
            )
        )

        mvc.perform(get("/v1/").asUser("a-user-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.profile").exists())
            .andExpect(jsonPath("$._links.countries").exists())
            .andExpect(jsonPath("$._links.activate.href", endsWith("/users/a-user-id")))
            .andExpect(jsonPath("$._links.createAccount").doesNotExist())
            .andExpect(jsonPath("$._links.accessRules").doesNotExist())
            .andExpect(jsonPath("$._links.searchAccessRules").doesNotExist())
            .andExpect(jsonPath("$._links.trackPageRendered").exists())
            .andExpect(jsonPath("$._links.organisations").doesNotExist())
            .andExpect(jsonPath("$._links.validateShareCode").exists())
    }

    @Test
    fun `registered user with profile and organization is set up`() {
        setSecurityContext("a-user-id")

        val organisationAccount =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.school()))
        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = "a-user-id"),
                profile = ProfileFactory.sample(),
                organisation = organisationAccount
            )
        )

        mvc.perform(get("/v1/").asUser("a-user-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.activate").doesNotExist())
            .andExpect(jsonPath("$._links.createAccount").doesNotExist())
            .andExpect(jsonPath("$._links.accessRules").doesNotExist())
            .andExpect(jsonPath("$._links.profile.href", endsWith("/users/a-user-id")))
            .andExpect(jsonPath("$._links.countries").exists())
            .andExpect(jsonPath("$._links.searchAccessRules").doesNotExist())
            .andExpect(jsonPath("$._links.trackPageRendered").exists())
            .andExpect(jsonPath("$._links.organisations").doesNotExist())
            .andExpect(jsonPath("$._links.account").doesNotExist())
            .andExpect(jsonPath("$._links.validateShareCode").exists())
    }

    @Test
    fun `registered lifetime user`() {
        setSecurityContext("a-user-id")

        val organisationAccount =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.school()))
        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = "a-user-id"),
                profile = ProfileFactory.sample(),
                organisation = organisationAccount,
                accessExpiresOn = null
            )
        )

        mvc.perform(get("/v1/").asUser("a-user-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.reportAccessExpired").doesNotExist())
    }

    @Test
    fun `registered user with an unexpired access period`() {
        setSecurityContext("a-user-id")

        val organisationAccount =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.school()))
        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = "a-user-id"),
                profile = ProfileFactory.sample(),
                organisation = organisationAccount,
                accessExpiresOn = ZonedDateTime.now().plusDays(1)
            )
        )

        mvc.perform(get("/v1/").asUser("a-user-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.reportAccessExpired").doesNotExist())
    }

    @Test
    fun `registered user with an expired access period`() {
        setSecurityContext("a-user-id")

        val organisationAccount =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.school()))
        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = "a-user-id"),
                profile = ProfileFactory.sample(),
                organisation = organisationAccount,
                accessExpiresOn = ZonedDateTime.now().minusDays(1)
            )
        )

        mvc.perform(get("/v1/").asUser("a-user-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.reportAccessExpired").exists())
            .andExpect(jsonPath("$._links.activate").doesNotExist())
            .andExpect(jsonPath("$._links.createAccount").doesNotExist())
            .andExpect(jsonPath("$._links.accessRules").doesNotExist())
            .andExpect(jsonPath("$._links.profile.href", endsWith("/users/a-user-id")))
            .andExpect(jsonPath("$._links.countries").exists())
            .andExpect(jsonPath("$._links.searchAccessRules").doesNotExist())
            .andExpect(jsonPath("$._links.trackPageRendered").exists())
            .andExpect(jsonPath("$._links.validateShareCode").exists())
    }

    @Test
    fun `user with VIEW_USERS role`() {
        mvc.perform(get("/v1/").asUserWithRoles("a-user-id", UserRoles.VIEW_USERS))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.user.href", containsString("/users/{id}")))
            .andExpect(jsonPath("$._links.user.templated", equalTo(true)))
    }

    @Test
    fun `user with VIEW_ACCESS_RULES role`() {
        mvc.perform(get("/v1/").asUserWithRoles("a-user-id", UserRoles.VIEW_ACCESS_RULES))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.searchAccessRules.href", endsWith("/access-rules{?name}")))
            .andExpect(jsonPath("$._links.searchAccessRules.templated", equalTo(true)))
    }

    @Test
    fun `user with VIEW_CONTENT_PACKAGES role`() {
        mvc.perform(get("/v1/").asUserWithRoles("a-user-id", UserRoles.VIEW_ACCESS_RULES))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.accessRules.href", endsWith("/users/{id}/access-rules")))
            .andExpect(jsonPath("$._links.accessRules.templated", equalTo(true)))
    }

    @Test
    fun `user with VIEW_ORGANISATIONS role`() {
        mvc.perform(get("/v1/").asUserWithRoles("a-user-id", UserRoles.VIEW_ORGANISATIONS))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.organisations").exists())
            .andExpect(jsonPath("$._links.organisations.templated", equalTo(true)))
            .andExpect(jsonPath("$._links.organisations.href", endsWith("/organisations{?name,countryCode,page,size}")))
            .andExpect(jsonPath("$._links.organisation.href", endsWith("/organisations/{id}")))
    }

    @Test
    fun `links use proto headers`() {
        mvc.perform(get("/v1/").header("x-forwarded-proto", "https"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.createAccount.href", startsWith("https")))
    }
}
