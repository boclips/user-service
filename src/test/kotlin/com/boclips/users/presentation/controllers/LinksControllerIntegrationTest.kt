package com.boclips.users.presentation.controllers

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.AccountFactory
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

class LinksControllerIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `anonymous user`() {
        mvc.perform(get("/v1/"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.activate").doesNotExist())
            .andExpect(jsonPath("$._links.user").doesNotExist())
            .andExpect(jsonPath("$._links.profile").doesNotExist())
            .andExpect(jsonPath("$._links.createAccount").exists())
            .andExpect(jsonPath("$._links.contracts").doesNotExist())
            .andExpect(jsonPath("$._links.countries").doesNotExist())
    }

    @Test
    fun `registered user`() {
        setSecurityContext("a-user-id")

        userRepository.save(
            UserFactory.sample(
                account = AccountFactory.sample(id = "a-user-id"),
                profile = null
            )
        )

        mvc.perform(get("/v1/").asUser("a-user-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.profile").exists())
            .andExpect(jsonPath("$._links.countries").exists())
            .andExpect(jsonPath("$._links.activate.href", endsWith("/users/a-user-id")))
            .andExpect(jsonPath("$._links.createAccount").doesNotExist())
            .andExpect(jsonPath("$._links.contracts").doesNotExist())
    }

    @Test
    fun `registered user with profile`() {
        setSecurityContext("a-user-id")

        userRepository.save(
            UserFactory.sample(
                account = AccountFactory.sample(id = "a-user-id"),
                profile = ProfileFactory.sample()
            )
        )

        mvc.perform(get("/v1/").asUser("a-user-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.activate").doesNotExist())
            .andExpect(jsonPath("$._links.createAccount").doesNotExist())
            .andExpect(jsonPath("$._links.contracts").doesNotExist())
            .andExpect(jsonPath("$._links.profile.href", endsWith("/users/a-user-id")))
            .andExpect(jsonPath("$._links.countries").exists())
    }

    @Test
    fun `user with VIEW_USERS role`() {
        mvc.perform(get("/v1/").asUserWithRoles("a-user-id", UserRoles.VIEW_USERS))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.user.href", containsString("/users/{id}")))
            .andExpect(jsonPath("$._links.user.templated", equalTo(true)))
    }

    @Test
    fun `user with VIEW_CONTRACTS role`() {
        mvc.perform(get("/v1/").asUserWithRoles("a-user-id", UserRoles.VIEW_CONTRACTS))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.contracts.href", containsString("/users/{id}/contracts")))
            .andExpect(jsonPath("$._links.contracts.templated", equalTo(true)))
    }

    @Test
    fun `links use proto headers`() {
        mvc.perform(get("/v1/").header("x-forwarded-proto", "https"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.createAccount.href", startsWith("https")))
    }
}
