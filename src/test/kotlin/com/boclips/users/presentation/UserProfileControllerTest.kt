package com.boclips.users.presentation

import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import com.boclips.users.testsupport.KeycloakUserFactory
import com.boclips.users.testsupport.asOperator
import com.boclips.users.testsupport.asUser
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class UserProfileControllerTest : AbstractSpringIntergrationTest() {

    @Autowired
    lateinit var identityProvider: IdentityProvider

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

    @Test
    fun `can get list of users`() {
        mvc.perform(get("/v1/users").asOperator())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.users").isArray)
                .andExpect(jsonPath("$.users[0]").exists())
                .andExpect(jsonPath("$.users[0].id").exists())
                .andExpect(jsonPath("$.users[0].activated").isBoolean)
    }

    @Test
    fun `can get list of users csv`() {
        val result = mvc.perform(get("/v1/users").accept("text/csv").asOperator())
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andReturn()

        print(result.response.contentAsString)
    }
}
