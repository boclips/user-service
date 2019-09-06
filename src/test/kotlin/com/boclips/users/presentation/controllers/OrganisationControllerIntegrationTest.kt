package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class OrganisationControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class Countries {
        @Test
        fun `lists all countries when user is authenticated`() {
            mvc.perform(get("/v1/countries").asUser("some-teacher"))
                .andExpect(jsonPath("$._embedded.countries.length()", greaterThanOrEqualTo(249)))
                .andExpect(jsonPath("$._embedded.countries[0].id", equalTo("AND")))
                .andExpect(jsonPath("$._embedded.countries[0].name", equalTo("Andorra")))
                .andExpect(jsonPath("$._embedded.countries[?(@.id == 'USA')].name", contains("United States")))
                .andExpect(
                    jsonPath(
                        "$._embedded.countries[?(@.id == 'USA')]._links.states.href",
                        contains(endsWith("/countries/USA/states"))
                    )
                )
                .andExpect(jsonPath("$._links.self.href", endsWith("/countries")))
        }

        @Test
        fun `cannot list all countries when not authenticated`() {
            mvc.perform(get("/v1/countries"))
                .andExpect(status().isForbidden)
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

    @Nested
    inner class CreatingOrganisations {
        @Test
        fun `returns a 403 response when user does not have an INSERT_ORGANISATIONS role`() {
            mvc.perform(
                post("/v1/organisations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ }")
                    .asUser("dont-have-roles@test.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `inserts an organisation and returns it's id in Location header`() {
            mvc.perform(
                post("/v1/organisations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "name": "Test Organisation",
                            "role": "ROLE_TEST_ORGANISATION",
                            "contractIds": ["A", "B", "C"]
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("has-role@test.com", UserRoles.INSERT_ORGANISATIONS)
            )
                .andExpect(status().isCreated)
                .andExpect(header().string("Location", containsString("/v1/organisations/")))
        }

        @Test
        fun `returns a 400 response when request data is invalid`() {
            mvc.perform(
                post("/v1/organisations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ }")
                    .asUserWithRoles("has-role@test.com", UserRoles.INSERT_ORGANISATIONS)
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.errors", hasSize<Int>(2)))
        }
    }

    @Nested
    inner class FetchingOrganisations {
        @Test
        fun `retrieves an organisation by id`() {
            val organisationName = "Test Org"
            val organisation = organisationRepository.save(
                organisationName = organisationName,
                role = "ROLE_TEST_ORG",
                contractIds = listOf(ContractId("A"), ContractId("B"), ContractId("C")),
                organisationType = OrganisationType.ApiCustomer
            )

            mvc.perform(
                get("/v1/organisations/${organisation.id.value}")
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name", equalTo(organisationName)))
                .andExpect(jsonPath("$.contractIds", containsInAnyOrder("A", "B", "C")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/organisations/${organisation.id.value}")))
        }

        @Test
        fun `returns a 403 response when caller does not have view organisations role`() {
            mvc.perform(
                get("/v1/organisations/some-org")
                    .asUser("has-role@test.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `returns a 404 response when organisation is not found by id`() {
            mvc.perform(
                get("/v1/organisations/this-does-not-exist")
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(status().isNotFound)
        }
    }
}