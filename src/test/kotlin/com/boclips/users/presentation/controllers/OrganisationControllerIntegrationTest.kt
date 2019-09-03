package com.boclips.users.presentation.controllers

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.mockito.internal.matchers.Matches
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class OrganisationControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `lists all countries when user is authenticated`() {
        mvc.perform(MockMvcRequestBuilders.get("/v1/countries").asUser("some-teacher"))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.countries", Matchers.hasSize<Int>(250)))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.countries[0].id", Matchers.equalTo("AND")))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.countries[0].name", Matchers.equalTo("Andorra")))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.countries[233].id", Matchers.equalTo("USA")))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.countries[233].name", Matchers.equalTo("United States")))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.countries[233]._links.states.href", Matchers.endsWith("/countries/USA/states")))
            .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href", Matchers.endsWith("/countries")))
    }

    @Test
    fun `cannot list all countries when not authenticated`() {
        mvc.perform(MockMvcRequestBuilders.get("/v1/countries"))
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun `lists all states when user is authenticated`() {
        mvc.perform(MockMvcRequestBuilders.get("/v1/countries/USA/states").asUser("some-teacher"))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.states", Matchers.hasSize<Int>(67)))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.states[0].id", Matchers.equalTo("AB")))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.states[0].name", Matchers.equalTo("Alberta")))
            .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href", Matchers.endsWith("/countries/USA/states")))
    }
}