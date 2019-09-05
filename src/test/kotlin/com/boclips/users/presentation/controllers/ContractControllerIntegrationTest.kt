package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ContractControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class CreatingContracts {
        @Test
        fun `returns a 403 response when user does not have an INSERT_CONTRACTS role`() {
            mvc.perform(
                post("/v1/contracts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ }")
                    .asUser("cant-create-contracts@hacker.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `creates a SelectedContent contract and returns it's location`() {
            mvc.perform(
                post("/v1/contracts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "type": "SelectedContent",
                            "name": "Test selected content contract",
                            "collectionIds": ["A", "B", "C"]
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("contract-creator@hacker.com", UserRoles.INSERT_CONTRACTS)
            )
                .andExpect(status().isCreated)
                .andExpect(header().string("Location", containsString("/v1/contracts/")))
        }

        @Test
        fun `returns a 400 response when SelectedContent payload is invalid`() {
            mvc.perform(
                post("/v1/contracts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "type": "SelectedContent"
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("contract-creator@hacker.com", UserRoles.INSERT_CONTRACTS)
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.errors", hasSize<Any>(2)))
        }

        @Test
        fun `returns a 400 response when contract type is not provided`() {
            mvc.perform(
                post("/v1/contracts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "name": "Test selected content contract",
                            "collectionIds": ["A", "B", "C"]
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("contract-creator@hacker.com", UserRoles.INSERT_CONTRACTS)
            )
                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    inner class FetchingContracts() {
        @Test
        fun `returns a 403 response when caller does not have a VIEW_CONTRACTS role`() {
            mvc.perform(
                get("/v1/contracts/some-contract-id").asUser("cant-view-contracts@hacker.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `returns requested contract`() {
            val contractName = "Super contract"
            val contract = selectedContentContractRepository.saveSelectedContentContract(
                contractName,
                listOf(CollectionId("A"))
            )

            mvc.perform(
                get("/v1/contracts/${contract.id.value}")
                    .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_CONTRACTS)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.type", equalTo("SelectedContent")))
                .andExpect(jsonPath("$.name", equalTo(contractName)))
                .andExpect(jsonPath("$.collectionIds", hasSize<Int>(1)))
                .andExpect(jsonPath("$.collectionIds[0]", equalTo("A")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/contracts/${contract.id.value}")))
        }

        @Test
        fun `returns a 404 response when given contract is not found`() {
            mvc.perform(
                get("/v1/contracts/this-does-not-exist")
                    .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_CONTRACTS)
            )
                .andExpect(status().isNotFound)
        }
    }
}
