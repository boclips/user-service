package com.boclips.users.config.security

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.UserFactory
import com.boclips.users.testsupport.asBackofficeUser
import com.boclips.users.testsupport.asUser
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class ApiSecurityConfigIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `only user managers can synchronize users`() {
        mvc.perform(MockMvcRequestBuilders.post("/v1/users/sync"))
            .andExpect(MockMvcResultMatchers.status().isForbidden)

        mvc.perform(MockMvcRequestBuilders.post("/v1/users/sync").asBackofficeUser())
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
    }

    @Test
    fun `everybody can access actuator without permissions`() {
        mvc.perform(MockMvcRequestBuilders.get("/actuator/health"))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
    }

    @Test
    fun `everybody can access links without permissions`() {
        mvc.perform(MockMvcRequestBuilders.get("/v1"))
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))

        mvc.perform(MockMvcRequestBuilders.get("/v1/"))
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
    }

    @Test
    fun `everybody can access any endpoint with OPTIONS`() {
        mvc.perform(MockMvcRequestBuilders.options("/v1/users/activate"))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
        mvc.perform(MockMvcRequestBuilders.options("/v1"))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
    }

    @Test
    fun `only authenticated users can activate accounts`() {
        mvc.perform(MockMvcRequestBuilders.post("/v1/users/activate"))
            .andExpect(MockMvcResultMatchers.status().isForbidden)

        saveUser(UserFactory.sample())
        mvc.perform(MockMvcRequestBuilders.post("/v1/users/activate").asUser("user-id"))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
    }
}