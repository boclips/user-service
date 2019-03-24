package com.boclips.users.config.security

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asBackofficeUser
import org.junit.jupiter.api.Test
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
}