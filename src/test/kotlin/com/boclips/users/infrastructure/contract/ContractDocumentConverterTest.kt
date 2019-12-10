package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.testsupport.factories.ContractDocumentFactory
import com.boclips.users.testsupport.factories.ContractFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ContractDocumentConverterTest {
    @Test
    fun `converts selected content contract document to contract`() {
        val document = ContractDocumentFactory.sampleSelectedCollectionsContractDocument(
            collectionIds = listOf("A", "B", "C")
        )

        val contract = converter.fromDocument(document) as Contract.SelectedContent

        assertThat(contract.id.value).isEqualTo(document.id.toHexString())
        assertThat(contract.name).isEqualTo(document.name)
        assertThat(contract.collectionIds).containsExactlyInAnyOrder(
            CollectionId("A"),
            CollectionId("B"),
            CollectionId("C")
        )
    }

    @Test
    fun `converts selected content contract to document`() {
        val contract = ContractFactory.sampleSelectedContentContract()

        val document = converter.toDocument(contract) as ContractDocument.SelectedCollections

        assertThat(document.id.toHexString()).isEqualTo(contract.id.value)
        assertThat(document.name).isEqualTo(contract.name)
        assertThat(document.collectionIds.map { CollectionId(it) }).isEqualTo(contract.collectionIds)
    }

    private val converter = ContractDocumentConverter()
}