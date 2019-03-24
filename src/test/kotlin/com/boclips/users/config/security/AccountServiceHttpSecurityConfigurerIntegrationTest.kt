package com.boclips.users.config.security

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.UserFactory
import com.boclips.users.testsupport.asUser
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AccountServiceHttpSecurityConfigurerIntegrationTest : AbstractSpringIntegrationTest() {

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
        mvc.perform(options("/v1/users/activate"))
            .andExpect(status().is2xxSuccessful)
        mvc.perform(options("/v1"))
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `only authenticated users can activate accounts`() {
        mvc.perform(post("/v1/users/activate"))
            .andExpect(status().isForbidden)

        val userId = saveUser(UserFactory.sample())
        mvc.perform(post("/v1/users/activate").asUser(userId))
            .andExpect(status().is2xxSuccessful)
    }
}