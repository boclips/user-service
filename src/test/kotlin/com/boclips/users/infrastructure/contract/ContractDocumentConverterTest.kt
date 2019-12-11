package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.VideoId
import com.boclips.users.testsupport.factories.ContractDocumentFactory
import com.boclips.users.testsupport.factories.ContractFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ContractDocumentConverterTest {
    @Nested
    inner class ConvertingSelectedCollections {
        @Test
        fun `converts document to domain object`() {
            val document = ContractDocumentFactory.sampleSelectedCollectionsContractDocument(
                collectionIds = listOf("A", "B", "C")
            )

            val contract = converter.fromDocument(document) as Contract.SelectedCollections

            assertThat(contract.id.value).isEqualTo(document.id.toHexString())
            assertThat(contract.name).isEqualTo(document.name)
            assertThat(contract.collectionIds).containsExactlyInAnyOrder(
                CollectionId("A"),
                CollectionId("B"),
                CollectionId("C")
            )
        }

        @Test
        fun `converts domain object to document`() {
            val contract = ContractFactory.sampleSelectedCollectionsContract()

            val document = converter.toDocument(contract) as ContractDocument.SelectedCollections

            assertThat(document.id.toHexString()).isEqualTo(contract.id.value)
            assertThat(document.name).isEqualTo(contract.name)
            assertThat(document.collectionIds.map { CollectionId(it) }).isEqualTo(contract.collectionIds)
        }
    }

    @Nested
    inner class ConvertingSelectedVideos {
        @Test
        fun `converts document to domain object`() {
            val document = ContractDocumentFactory.sampleSelectedVideosContractDocument(
                videoIds = listOf("A", "B", "C")
            )

            val contract = converter.fromDocument(document) as Contract.SelectedVideos

            assertThat(contract.id.value).isEqualTo(document.id.toHexString())
            assertThat(contract.name).isEqualTo(document.name)
            assertThat(contract.videoIds).containsExactlyInAnyOrder(
                VideoId("A"),
                VideoId("B"),
                VideoId("C")
            )
        }

        @Test
        fun `converts domain object to document`() {
            val contract = ContractFactory.sampleSelectedVideosContract()

            val document = converter.toDocument(contract) as ContractDocument.SelectedVideos

            assertThat(document.id.toHexString()).isEqualTo(contract.id.value)
            assertThat(document.name).isEqualTo(contract.name)
            assertThat(document.videoIds.map { VideoId(it) }).isEqualTo(contract.videoIds)
        }
    }

    private val converter = ContractDocumentConverter()
}