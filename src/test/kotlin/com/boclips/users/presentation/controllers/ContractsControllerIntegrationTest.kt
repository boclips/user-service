package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import org.hamcrest.Matchers
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder

class ContractsControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class SearchingContracts {
        @Test
        fun `returns a 403 response when caller does not have a VIEW_CONTRACTS role`() {
            mvc.perform(
                get("/v1/contracts?name=Super+Contract")
                    .asUser("cant-view-contracts@hacker.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `returns given contract on the list when the name matches`() {
            val contractName = "Super contract"
            val contract = selectedContentContractRepository.saveSelectedContentContract(
                contractName,
                listOf(CollectionId("A"))
            )

            mvc.perform(
                get(
                    UriComponentsBuilder.fromUriString("/v1/contracts")
                        .queryParam("name", contractName)
                        .build()
                        .toUri()
                )
                    .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_CONTRACTS)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.contracts", hasSize<Any>(1)))
                .andExpect(jsonPath("$._embedded.contracts[0].type", Matchers.equalTo("SelectedContent")))
                .andExpect(jsonPath("$._embedded.contracts[0].type", Matchers.equalTo("SelectedContent")))
                .andExpect(jsonPath("$._embedded.contracts[0].name", Matchers.equalTo(contractName)))
                .andExpect(jsonPath("$._embedded.contracts[0].collectionIds", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.contracts[0].collectionIds[0]", Matchers.equalTo("A")))
                .andExpect(
                    jsonPath(
                        "$._embedded.contracts[0]._links.self.href",
                        endsWith("/v1/contracts/${contract.id.value}")
                    )
                )
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/contracts?name=Super%20contract")))
        }

        @Test
        fun `returns an empty list when contract is not found by name`() {
            selectedContentContractRepository.saveSelectedContentContract(
                "Super contract",
                listOf(CollectionId("A"))
            )

            mvc.perform(
                get(
                    UriComponentsBuilder.fromUriString("/v1/contracts")
                        .queryParam("name", "this does not exist")
                        .build()
                        .toUri()
                )
                    .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_CONTRACTS)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.contracts", hasSize<Any>(0)))
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/contracts?name=this%20does%20not%20exist")))
        }

        @Test
        fun `returns an empty list when lookup is done with a blank parameter`() {
            selectedContentContractRepository.saveSelectedContentContract(
                "Super contract",
                listOf(CollectionId("A"))
            )

            mvc.perform(
                get(
                    UriComponentsBuilder.fromUriString("/v1/contracts")
                        .queryParam("name", "")
                        .build()
                        .toUri()
                )
                    .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_CONTRACTS)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.contracts", hasSize<Any>(0)))
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/contracts?name=")))
        }

        @Test
        fun `returns all contracts in the system when name query parameter is not provided`() {
            val firstContractName = "first"
            val firstContract = selectedContentContractRepository.saveSelectedContentContract(
                firstContractName,
                listOf(CollectionId("A"))
            )
            val secondContractName = "first"
            val secondContract = selectedContentContractRepository.saveSelectedContentContract(
                secondContractName,
                listOf(CollectionId("B"))
            )

            mvc.perform(
                get("/v1/contracts")
                    .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_CONTRACTS)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.contracts", hasSize<Any>(2)))
                .andExpect(jsonPath("$._embedded.contracts[0].type", Matchers.equalTo("SelectedContent")))
                .andExpect(jsonPath("$._embedded.contracts[0].type", Matchers.equalTo("SelectedContent")))
                .andExpect(jsonPath("$._embedded.contracts[0].name", Matchers.equalTo(firstContractName)))
                .andExpect(jsonPath("$._embedded.contracts[0].collectionIds", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.contracts[0].collectionIds[0]", Matchers.equalTo("A")))
                .andExpect(
                    jsonPath(
                        "$._embedded.contracts[0]._links.self.href",
                        endsWith("/v1/contracts/${firstContract.id.value}")
                    )
                )
                .andExpect(jsonPath("$._embedded.contracts[1].type", Matchers.equalTo("SelectedContent")))
                .andExpect(jsonPath("$._embedded.contracts[1].type", Matchers.equalTo("SelectedContent")))
                .andExpect(jsonPath("$._embedded.contracts[1].name", Matchers.equalTo(secondContractName)))
                .andExpect(jsonPath("$._embedded.contracts[1].collectionIds", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.contracts[1].collectionIds[0]", Matchers.equalTo("B")))
                .andExpect(
                    jsonPath(
                        "$._embedded.contracts[1]._links.self.href",
                        endsWith("/v1/contracts/${secondContract.id.value}")
                    )
                )
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/contracts{?name}")))
        }
    }
}
