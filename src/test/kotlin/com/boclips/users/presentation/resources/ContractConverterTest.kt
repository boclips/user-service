package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.testsupport.factories.ContractFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ContractConverterTest {
    @Test
    fun `converts selected content contract`() {
        val contract = ContractFactory.sampleSelectedContentContract(
            collectionIds = listOf(CollectionId("A"), CollectionId("B"))
        )

        val resource = converter.toResource(contract) as ContractResource.SelectedContent

        assertThat(resource.id).isNotBlank()
        assertThat(resource.name).isEqualTo(contract.name)
        assertThat(resource.collectionIds).containsExactlyInAnyOrder("A", "B")
    }

    private val converter = ContractConverter()
}