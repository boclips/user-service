package com.boclips.users.presentation

import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import com.boclips.users.testsupport.UserFactory
import com.boclips.users.testsupport.asUser
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.startsWith
import org.junit.jupiter.api.BeforeEach
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
        val user =
            keycloakClientFake.createUserIfDoesntExist(UserFactory.sample(activated = true))

        mvc.perform(get("/v1/").asUser(user.keycloakId.value))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.profile.href", endsWith(user.keycloakId.value)))
    }
}