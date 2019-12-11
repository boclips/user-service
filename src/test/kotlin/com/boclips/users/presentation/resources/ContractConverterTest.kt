package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.VideoId
import com.boclips.users.presentation.hateoas.ContractLinkBuilder
import com.boclips.users.presentation.hateoas.SelectedContractLinkBuilder
import com.boclips.users.presentation.resources.converters.ContractConverter
import com.boclips.users.testsupport.factories.ContractFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ContractConverterTest {
    @Test
    fun `converts selected collections contract`() {
        val contract = ContractFactory.sampleSelectedCollectionsContract(
            collectionIds = listOf(CollectionId("A"), CollectionId("B"))
        )

        val resource = converter.toResource(contract) as ContractResource.SelectedCollections

        assertThat(resource.name).isEqualTo(contract.name)
        assertThat(resource.collectionIds).containsExactlyInAnyOrder("A", "B")
    }

    @Test
    fun `converts selected videos contract`() {
        val contract = ContractFactory.sampleSelectedVideosContract(
            videoIds = listOf(VideoId("A"), VideoId("B"))
        )

        val resource = converter.toResource(contract) as ContractResource.SelectedVideos

        assertThat(resource.name).isEqualTo(contract.name)
        assertThat(resource.videoIds).containsExactlyInAnyOrder("A", "B")
    }

    private val converter = ContractConverter(
        ContractLinkBuilder(),
        SelectedContractLinkBuilder()
    )
}
