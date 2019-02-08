package com.boclips.users.config.security

import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import com.boclips.users.testsupport.asUser
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserServiceHttpSecurityConfigurerIntegrationTest : AbstractSpringIntergrationTest() {

    @Test
    fun `everybody can access actuator without permissions`() {
        mvc.perform(get("/actuator/health"))
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `everybody can access links without permissions`() {
        mvc.perform(get("/v1"))
            .andExpect(status().`is`(HttpStatus.OK.value()))

        mvc.perform(get("/v1/"))
            .andExpect(status().`is`(HttpStatus.OK.value()))
    }

    @Test
    fun `everybody can access any endpoint with OPTIONS`() {
        mvc.perform(options("/v1/users"))
            .andExpect(status().is2xxSuccessful)
        mvc.perform(options("/v1"))
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `only authenticated users can activate accounts`() {
        mvc.perform(post("/v1/users"))
            .andExpect(status().isForbidden)

        mvc.perform(post("/v1/users").asUser("user-id"))
            .andExpect(status().is2xxSuccessful)
    }
}