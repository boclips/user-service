package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoSelectedContentContractRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `saves and retrieves a selected content contract`() {
        val persistedRepository = selectedContentContractRepository.saveSelectedContentContract(
            name = "Test selected content contract",
            collectionIds = listOf(CollectionId("A"), CollectionId("B"), CollectionId("C"))
        )

        val fetchedRepository = selectedContentContractRepository.findById(persistedRepository.id)

        assertThat(fetchedRepository).isEqualTo(persistedRepository)
    }

    @Test
    fun `returns null if contract is not found by id`() {
        assertThat(selectedContentContractRepository.findById(ContractId("this does not exist"))).isNull()
    }
}
