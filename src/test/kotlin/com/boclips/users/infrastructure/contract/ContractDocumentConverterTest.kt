package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.testsupport.factories.ContractDocumentFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ContractDocumentConverterTest {
    @Test
    fun `converts selected content contract document to contract`() {
        val document = ContractDocumentFactory.sampleSelectedContentContractDocument(
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

    private val converter = ContractDocumentConverter()
}