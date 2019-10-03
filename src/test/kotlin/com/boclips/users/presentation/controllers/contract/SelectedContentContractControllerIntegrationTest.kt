package com.boclips.users.presentation.controllers.contract

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class SelectedContentContractControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class AddingCollections {
        @Test
        fun `returns a 403 response if caller does not have UPDATE_CONTRACTS role`() {
            val contractId = "test-contract-id"
            val collectionId = "test-collection-id"

            mvc.perform(
                put("/v1/selected-content-contracts/$contractId/collections/$collectionId").asUser("no-roles@doh.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `adds the collection to the contract`() {
            val contractId = selectedContentContractRepository.saveSelectedContentContract(
                "Some test contract",
                emptyList()
            ).id
            val collectionId = "test-collection-id"

            mvc.perform(
                put("/v1/selected-content-contracts/${contractId.value}/collections/$collectionId")
                    .asUserWithRoles(
                        "test@user.com",
                        UserRoles.UPDATE_CONTRACTS
                    )
            )
                .andExpect(status().isNoContent)

            val updatedContract = contractRepository.findById(contractId) as Contract.SelectedContent

            assertThat(updatedContract.collectionIds).contains(CollectionId(collectionId))
        }

        @Test
        fun `returns a 404 response if contract is not found`() {
            mvc.perform(
                put("/v1/selected-content-contracts/does-not-exist/collections/collection-id")
                    .asUserWithRoles(
                        "test@user.com",
                        UserRoles.UPDATE_CONTRACTS
                    )
            )
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    inner class RemovingCollections {
        @Test
        fun `returns a 403 response if caller does not have UPDATE_CONTRACTS role`() {
            val contractId = "test-contract-id"
            val collectionId = "test-collection-id"

            mvc.perform(
                delete("/v1/selected-content-contracts/$contractId/collections/$collectionId").asUser("no-roles@doh.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `removes provided collection from a contract`() {
            val collectionId = "test-collection-id"
            val contractId = selectedContentContractRepository.saveSelectedContentContract(
                "Some test contract",
                listOf(CollectionId(collectionId))
            ).id

            mvc.perform(
                delete("/v1/selected-content-contracts/${contractId.value}/collections/$collectionId")
                    .asUserWithRoles(
                        "test@user.com",
                        UserRoles.UPDATE_CONTRACTS
                    )
            )
                .andExpect(status().isNoContent)

            val updatedContract = contractRepository.findById(contractId) as Contract.SelectedContent

            assertThat(updatedContract.collectionIds).isEmpty()
        }

        @Test
        fun `returns 404 when contract does not exist`() {
            mvc.perform(
                delete("/v1/selected-content-contracts/does-not-exist/collections/collection-id")
                    .asUserWithRoles(
                        "test@user.com",
                        UserRoles.UPDATE_CONTRACTS
                    )
            )
                .andExpect(status().isNotFound)
        }
    }
}
