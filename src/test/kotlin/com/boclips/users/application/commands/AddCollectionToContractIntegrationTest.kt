package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.ContractNotFoundException
import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AddCollectionToContractIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `adds collection to contract and is idempotent`() {
        val existingId = CollectionId("some-existing-id")
        val contract = selectedContentContractRepository.saveSelectedCollectionsContract(
            "whatever",
            listOf(existingId)
        )

        val newId = CollectionId("another-id")
        addCollectionToContract(contractId = contract.id, collectionId = newId)
        addCollectionToContract(contractId = contract.id, collectionId = newId)

        val updatedContract = contractRepository.findById(contract.id) as Contract.SelectedCollections

        assertThat(updatedContract.collectionIds).containsOnly(existingId, newId)
    }

    @Test
    fun `throws ContractNotFoundException when contract is not found`() {
        assertThrows<ContractNotFoundException> {
            addCollectionToContract(
                contractId = ContractId("does not exist"),
                collectionId = CollectionId("another-id")
            )
        }
    }
}
