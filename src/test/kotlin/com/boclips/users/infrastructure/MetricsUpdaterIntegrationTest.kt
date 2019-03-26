package com.boclips.users.infrastructure

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.UserFactory
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MetricsUpdaterIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `expose users and identities`() {
        saveUser(UserFactory.sample())

        mockMvc.perform(get("/actuator/prometheus"))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("boclips_users_count")))
            .andExpect(content().string(containsString("boclips_identities_count")))
    }
}