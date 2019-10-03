package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.ContractNotFoundException
import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class RemoveCollectionFromContractIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var removeCollectionFromContract: RemoveCollectionFromContract

    @Test
    fun `can remove a collection from a contract`() {
        val existingId = CollectionId("some-existing-id")
        val contract = selectedContentContractRepository.saveSelectedContentContract(
            "whatever",
            listOf(existingId)
        )

        removeCollectionFromContract(contractId = contract.id, collectionId = existingId)

        val updatedContract = contractRepository.findById(contract.id) as Contract.SelectedContent

        assertThat(updatedContract.collectionIds).isEmpty()
    }

    @Test
    fun `throws a not found exception when contract is not found`() {
        assertThrows<ContractNotFoundException> {
            removeCollectionFromContract(
                contractId = ContractId("does not exist"),
                collectionId = CollectionId("anything")
            )
        }
    }

    @Test
    fun `does not fail when collection to remove is missing (is idempotent)`() {
        val contract = selectedContentContractRepository.saveSelectedContentContract(
            "whatever",
            emptyList()
        )

        removeCollectionFromContract(contractId = contract.id, collectionId = CollectionId("some-id"))

        val updatedContract = contractRepository.findById(contract.id) as Contract.SelectedContent

        assertThat(updatedContract.collectionIds).isEmpty()
    }
}