package com.boclips.users.presentation.controllers

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CountryControllerIntegrationTest : AbstractSpringIntegrationTest() {

    companion object {
        const val USA = "_embedded.countries[?(@.id == 'USA')]"
    }

    @Test
    fun `lists all countries when user is authenticated`() {
        mvc.perform(get("/v1/countries").asUser("some-teacher"))
            .andExpect(jsonPath("$._embedded.countries.length()", greaterThanOrEqualTo(249)))
            .andExpect(jsonPath("$._embedded.countries[0].id", equalTo("AND")))
            .andExpect(jsonPath("$._embedded.countries[0].name", equalTo("Andorra")))
            .andExpect(jsonPath("$.${USA}.name", contains("United States")))
            .andExpect(jsonPath("$.${USA}.states", contains(hasSize<Int>(67))))
            .andExpect(jsonPath("$.${USA}.states[0].id", contains(equalTo("AB"))))
            .andExpect(jsonPath("$.${USA}.states[0].name", contains(equalTo("Alberta"))))
            .andExpect(jsonPath("$.${USA}._links.states.href", contains(endsWith("/countries/USA/states"))))
            .andExpect(jsonPath("$.${USA}._links.schools.href", contains(endsWith("/schools?countryCode=USA{&query,state}"))))
            .andExpect(jsonPath("$._links.self.href", endsWith("/countries")))
    }

    @Test
    fun `cannot list all countries when not authenticated`() {
        mvc.perform(get("/v1/countries")).andExpect(status().isForbidden)
    }

    @Test
    fun `lists all states when user is authenticated`() {
        mvc.perform(get("/v1/countries/USA/states").asUser("some-teacher"))
            .andExpect(jsonPath("$._embedded.states", hasSize<Int>(67)))
            .andExpect(jsonPath("$._embedded.states[0].id", equalTo("AB")))
            .andExpect(jsonPath("$._embedded.states[0].name", equalTo("Alberta")))
            .andExpect(jsonPath("$._links.self.href", endsWith("/countries/USA/states")))
    }
}