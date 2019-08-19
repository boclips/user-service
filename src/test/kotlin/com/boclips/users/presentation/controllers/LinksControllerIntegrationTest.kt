package com.boclips.users.presentation.controllers

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.UserFactory
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import org.hamcrest.Matchers.endsWith
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
//    TODO include when frontend is refactored        .andExpect(jsonPath("$._links.profile").doesNotExist())
            .andExpect(jsonPath("$._links.createAccount").exists())
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
            .andExpect(jsonPath("$._links.activate.href", endsWith("/users/a-user-id")))
            .andExpect(jsonPath("$._links.createAccount").doesNotExist())
    }

    @Test
    fun `activated user`() {
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
            .andExpect(jsonPath("$._links.profile.href", endsWith("/users/a-user-id")))
    }

    @Test
    fun `links use proto headers`() {
        mvc.perform(get("/v1/").header("x-forwarded-proto", "https"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.createAccount.href", startsWith("https")))
    }
}
