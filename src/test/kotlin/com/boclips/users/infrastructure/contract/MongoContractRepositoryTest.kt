package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MongoContractRepositoryTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class FindById {
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
    }

    @Nested
    inner class FindAllByName {
        @Test
        fun `looks up contracts by name and deserializes them to a correct class`() {
            val contractName = "Name Test"
            val persistedContract = selectedContentContractRepository.saveSelectedContentContract(
                name = contractName,
                collectionIds = listOf(CollectionId("A"), CollectionId("B"), CollectionId("C"))
            )

            val foundContracts = contractRepository.findAllByName(contractName)

            assertThat(foundContracts).containsOnly(persistedContract)
        }

        @Test
        fun `returns an empty list if no contract is found by name`() {
            assertThat(contractRepository.findAllByName("this does not exist")).isEmpty()
        }
    }

    @Nested
    inner class FindAll {
        @Test
        fun `returns all contracts`() {
            selectedContentContractRepository.saveSelectedContentContract(name = "Hey", collectionIds = emptyList())
            selectedContentContractRepository.saveSelectedContentContract(name = "Ho", collectionIds = emptyList())

            val allContracts = contractRepository.findAll()

            assertThat(allContracts).hasSize(2).extracting("name").containsExactlyInAnyOrder("Hey", "Ho")
        }

        @Test
        fun `returns an empty list if no contracts are found`() {
            assertThat(contractRepository.findAll()).isEmpty()
        }
    }
}
