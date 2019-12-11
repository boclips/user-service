package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.contract.VideoId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MongoContractRepositoryTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class FindById {
        @Test
        fun `fetches a collection contract by id and deserializes it to a correct class`() {
            val persistedContract = selectedContentContractRepository.saveSelectedCollectionsContract(
                name = "Test selected content contract",
                collectionIds = listOf(CollectionId("A"), CollectionId("B"), CollectionId("C"))
            )

            val fetchedContract = contractRepository.findById(persistedContract.id)

            assertThat(fetchedContract).isEqualTo(persistedContract)
        }

        @Test
        fun `fetches a video contract by id and deserializes it to the correct class`() {
            val persistedContract = selectedContentContractRepository.saveSelectedVideosContract(
                name = "Test selected content contract",
                videoIds = listOf(VideoId("A"), VideoId("B"), VideoId("C"))
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
            val persistedContract = selectedContentContractRepository.saveSelectedCollectionsContract(
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
            val firstCollectionContract = selectedContentContractRepository.saveSelectedCollectionsContract(
                name = "Hey",
                collectionIds = emptyList()
            )
            val secondCollectionContract = selectedContentContractRepository.saveSelectedCollectionsContract(
                name = "Ho",
                collectionIds = emptyList()
            )
            val firstVideoContract =
                selectedContentContractRepository.saveSelectedVideosContract(name = "Yo", videoIds = emptyList())

            val allContracts = contractRepository.findAll()

            assertThat(allContracts).hasSize(3)
            assertThat(allContracts).containsExactlyInAnyOrder(
                firstCollectionContract,
                secondCollectionContract,
                firstVideoContract
            )
        }

        @Test
        fun `returns an empty list if no contracts are found`() {
            assertThat(contractRepository.findAll()).isEmpty()
        }
    }
}
