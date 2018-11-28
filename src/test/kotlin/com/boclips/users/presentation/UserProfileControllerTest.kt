package com.boclips.users.presentation

import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import com.boclips.users.testsupport.asUser
import org.hamcrest.Matchers
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class UserProfileControllerTest : AbstractSpringIntergrationTest() {

    @Test
    fun `activate user endpoint`() {
        mvc.perform(post("/v1/users").asUser("activated-user"))
                .andExpect(status().isOk)
    }

    @Test
    fun `activate user endpoint contains self link`() {
        mvc.perform(post("/v1/users").asUser("activated-user"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._links.self.href", endsWith("/users/activated-user")))
    }

    @Test
    fun `activated user gets the correct links`() {
        mvc.perform(post("/v1/users").asUser("activated-user"))
                .andExpect(status().isOk)

        mvc.perform(get("/v1/").asUser("activated-user"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._links.profile.href", endsWith("/users/activated-user")))
    }
}