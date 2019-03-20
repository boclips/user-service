package com.boclips.users.presentation

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.startsWith
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class LinksControllerTest : AbstractSpringIntegrationTest() {

    @Test
    fun `anonymous user`() {
        mvc.perform(get("/v1/"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.activate").doesNotExist())
            .andExpect(jsonPath("$._links.profile").doesNotExist())
            .andExpect(jsonPath("$._links.createAccount").exists())
    }

    @Test
    fun `registered user`() {
        setSecurityContext("a-user-id")

        accountRepository.save(
            Account(
                id = AccountId(value = "a-user-id"),
                activated = false,
                subjects = "irrelevant",
                analyticsId = AnalyticsId(value = "irrelevant"),
                isReferral = false,
                referralCode = null
            )
        )

        mvc.perform(get("/v1/").asUser("a-user-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.createAccount").doesNotExist())
            .andExpect(jsonPath("$._links.profile").doesNotExist())
            .andExpect(jsonPath("$._links.activate.href", endsWith("/users/activate")))
    }

    @Test
    fun `activated user`() {
        setSecurityContext("a-user-id")

        accountRepository.save(
            Account(
                id = AccountId(value = "a-user-id"),
                activated = true,
                subjects = "irrelevant",
                analyticsId = AnalyticsId(value = "irrelevant"),
                isReferral = false,
                referralCode = null
            )
        )

        mvc.perform(get("/v1/").asUser("a-user-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.activate").doesNotExist())
            .andExpect(jsonPath("$._links.createAccount").doesNotExist())
            .andExpect(jsonPath("$._links.profile.href", endsWith("/users/{id}")))
            .andExpect(jsonPath("$._links.profile.templated", equalTo(true)))
    }

    @Test
    fun `links use proto headers`() {
        setSecurityContext("a-user-id")

        mvc.perform(get("/v1/").asUser("unknown-user").header("x-forwarded-proto", "https"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.activate.href", startsWith("https")))
    }
}