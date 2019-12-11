package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.VideoId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoSelectedContentContractRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `saves a selected collections contract`() {
        val contractName = "Test selected content contract"
        val persistedContract = selectedContentContractRepository.saveSelectedCollectionsContract(
            name = contractName,
            collectionIds = listOf(CollectionId("A"), CollectionId("B"), CollectionId("C"))
        )

        assertThat(persistedContract.id).isNotNull
        assertThat(persistedContract.name).isEqualTo(contractName)
        assertThat(persistedContract.collectionIds).containsOnly(
            CollectionId("A"),
            CollectionId("B"),
            CollectionId("C")
        )
    }

    @Test
    fun `saves a selected videos contract`() {
        val contractName = "Test selected content contract"
        val persistedContract = selectedContentContractRepository.saveSelectedVideosContract(
            name = contractName,
            videoIds = listOf(VideoId("A"), VideoId("B"), VideoId("C"))
        )

        assertThat(persistedContract.id).isNotNull
        assertThat(persistedContract.name).isEqualTo(contractName)
        assertThat(persistedContract.videoIds).containsOnly(VideoId("A"), VideoId("B"), VideoId("C"))
    }
}
