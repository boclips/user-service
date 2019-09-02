package com.boclips.users.presentation.controllers

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class SchoolControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `lists all countries when user is authenticated`() {
        mvc.perform(MockMvcRequestBuilders.get("/v1/countries").asUser("some-teacher"))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.countries", Matchers.hasSize<Int>(250)))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.countries[0].id", Matchers.equalTo("AD")))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.countries[0].name", Matchers.equalTo("Andorra")))
            .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href", Matchers.endsWith("/countries")))
    }

    @Test
    fun `cannot list all countries when not authenticated`() {
        mvc.perform(MockMvcRequestBuilders.get("/v1/countries"))
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }
}