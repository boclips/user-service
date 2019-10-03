package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder

class OrganisationTestSupportControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class CreatingOrganisations {
        @Test
        fun `returns a 403 response when user does not have an INSERT_ORGANISATIONS role`() {
            mvc.perform(
                post("/v1/api-integrations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ }")
                    .asUser("dont-have-roles@test.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `inserts an organisation and returns it's id in Location header`() {
            mvc.perform(
                post("/v1/api-integrations")
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
                .andExpect(header().string("Location", containsString("/organisations/")))
        }

        @Test
        fun `returns a 409 response when organisation name collides`() {
            val organisationName = "Test Org"
            organisationAccountRepository.save(
                apiIntegration = OrganisationFactory.apiIntegration(name = organisationName),
                contractIds = emptyList(),
                role = "ROLE_TEST_ORG"
            )

            mvc.perform(
                post("/v1/api-integrations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "name": "$organisationName",
                            "role": "ROLE_ANOTHER",
                            "contractIds": []
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("has-role@test.com", UserRoles.INSERT_ORGANISATIONS)
            )
                .andExpect(status().isConflict)
        }

        @Test
        fun `returns a 409 response when organisation role collides`() {
            val role = "ROLE_TEST_ORG"
            organisationAccountRepository.save(
                apiIntegration = OrganisationFactory.apiIntegration(name = "Some name"),
                contractIds = emptyList(),
                role = role
            )

            mvc.perform(
                post("/v1/api-integrations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "name": "Some other name",
                            "role": "$role",
                            "contractIds": []
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("has-role@test.com", UserRoles.INSERT_ORGANISATIONS)
            )
                .andExpect(status().isConflict)
        }

        @Test
        fun `returns a 400 response when request data is invalid`() {
            mvc.perform(
                post("/v1/api-integrations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ }")
                    .asUserWithRoles("has-role@test.com", UserRoles.INSERT_ORGANISATIONS)
            )
                .andExpect(status().isBadRequest)
                .andExpectApiErrorPayload()
        }
    }

    @Nested
    inner class FetchingOrganisationsById {
        @Test
        fun `retrieves an organisation account by id`() {
            val organisationName = "Test Org"
            val organisation = organisationAccountRepository.save(
                apiIntegration = OrganisationFactory.apiIntegration(name = organisationName),
                contractIds = listOf(ContractId("A"), ContractId("B"), ContractId("C")),
                role = "ROLE_TEST_ORG"
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
        fun `returns a 404 response when organisation account is not found by id`() {
            mvc.perform(
                get("/v1/organisations/this-does-not-exist")
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    inner class FetchingOrganisationsByName {
        @Test
        fun `returns a 403 response when caller does not have a VIEW_ORGANISATIONS role`() {
            mvc.perform(
                get(
                    UriComponentsBuilder.fromUriString("/v1/api-integrations")
                        .queryParam("name", "Some org that does not exist")
                        .build()
                        .toUri()
                )
                    .asUser("has-role@test.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `returns given organisation when it's found by name`() {
            val organisationName = "Test Org"
            val organisation = organisationAccountRepository.save(
                apiIntegration = OrganisationFactory.apiIntegration(name = organisationName),
                contractIds = listOf(ContractId("A"), ContractId("B"), ContractId("C")),
                role = "ROLE_TEST_ORG"
            )

            mvc.perform(
                get(
                    UriComponentsBuilder.fromUriString("/v1/api-integrations")
                        .queryParam("name", organisationName)
                        .build()
                        .toUri()
                )
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name", equalTo(organisationName)))
                .andExpect(jsonPath("$.contractIds", containsInAnyOrder("A", "B", "C")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/organisations/${organisation.id.value}")))
        }

        @Test
        fun `returns a 404 response when organisation is not found by name`() {
            mvc.perform(
                get(
                    UriComponentsBuilder.fromUriString("/v1/api-integrations")
                        .queryParam("name", "Some org that does not exist")
                        .build()
                        .toUri()
                )
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `returns a 400 response when query parameter is not provided`() {
            mvc.perform(
                get("/v1/api-integrations")
                    .asUserWithRoles("viewer@hacker.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(status().isBadRequest)
        }
    }
}