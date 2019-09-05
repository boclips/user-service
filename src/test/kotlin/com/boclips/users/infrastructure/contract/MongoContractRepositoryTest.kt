package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoContractRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `fetches a contract by id and deserializes it to a correct class`() {
        val persistedContract = selectedContentContractRepository.saveSelectedContentContract(
            name = "Test selected content contract",
            collectionIds = listOf(CollectionId("A"), CollectionId("B"), CollectionId("C"))
        )

        val fetchedContract = contractRepository.findById(persistedContract.id)

        assertThat(fetchedContract).isEqualTo(persistedContract)
    }

    @Test
    fun `returns null if contract is not found by id`() {
        assertThat(contractRepository.findById(ContractId("this does not exist"))).isNull()
    }

    @Test
    fun `fetches a contract by name and deserializes it to a correct class`() {
        val contractName = "Name Test"
        val persistedContract = selectedContentContractRepository.saveSelectedContentContract(
            name = contractName,
            collectionIds = listOf(CollectionId("A"), CollectionId("B"), CollectionId("C"))
        )

        val fetchedContract = contractRepository.findByName(contractName)

        assertThat(fetchedContract).isEqualTo(persistedContract)
    }

    @Test
    fun `returns null if contract is not found by name`() {
        assertThat(contractRepository.findByName("this does not exist")).isNull()
    }
}
