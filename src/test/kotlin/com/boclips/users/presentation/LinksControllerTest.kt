package com.boclips.users.presentation

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.analytics.MixpanelId
import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import com.boclips.users.testsupport.asUser
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.startsWith
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class LinksControllerTest : AbstractSpringIntergrationTest() {

    @Test
    fun `GET links when unknown user returns activation link`() {
        mvc.perform(get("/v1/").asUser("unknown-user"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.activate.href", endsWith("/users")))
    }

    @Test
    fun `GET links uses proto headers`() {
        mvc.perform(get("/v1/").asUser("unknown-user").header("x-forwarded-proto", "https"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.activate.href", startsWith("https")))
    }

    @Test
    fun `GET links when anonymous user returns empty links`() {
        mvc.perform(get("/v1/"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links").doesNotExist())
    }

    @Test
    fun `GET links when activated user returns profile link`() {
        accountRepository.save(
            Account(
                id = AccountId(value = "a-user-id"),
                activated = true,
                subjects = "irrelevant",
                analyticsId = MixpanelId(value = "irrelevant")
            )
        )

        mvc.perform(get("/v1/").asUser("a-user-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.profile.href", endsWith("/users/a-user-id")))
    }
}