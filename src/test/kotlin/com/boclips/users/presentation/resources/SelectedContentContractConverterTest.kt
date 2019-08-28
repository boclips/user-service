package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.testsupport.factories.SelectedContentContractFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SelectedContentContractConverterTest {
    @Test
    fun `converts from contract id`() {
        val document = SelectedContentContractFactory.sample(
            collectionIds = listOf(CollectionId("A"), CollectionId("B"))
        )

        assertThat(converter.convert(document).id).isNotBlank()
        assertThat(converter.convert(document).name).isEqualTo(document.name)
        assertThat(converter.convert(document).collectionIds).containsExactlyInAnyOrder("A", "B")
    }

    private val converter = SelectedContentContractConverter()
}