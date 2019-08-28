package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.testsupport.factories.SelectedContentContractDocumentFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SelectedContentContractDocumentConverterTest {
    @Nested
    inner class SelectedContentContract {
        @Test
        fun `converts document to contract`() {
            val document = SelectedContentContractDocumentFactory.sample(
                collectionIds = listOf("A", "B", "C")
            )

            val contract = converter.fromDocument(document)

            assertThat(contract.id.value).isEqualTo(document.id.toHexString())
            assertThat(contract.name).isEqualTo(document.name)
            assertThat(contract.collectionIds).containsExactlyInAnyOrder(
                CollectionId("A"),
                CollectionId("B"),
                CollectionId("C")
            )
        }
    }

    private val converter = SelectedContentContractDocumentConverter()
}