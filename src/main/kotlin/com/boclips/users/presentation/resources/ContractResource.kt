package com.boclips.users.presentation.resources

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.hateoas.ResourceSupport

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = ContractResource.SelectedCollections::class, name = "SelectedCollections"),
    JsonSubTypes.Type(value = ContractResource.SelectedVideos::class, name = "SelectedVideos")
)
sealed class ContractResource : ResourceSupport() {
    data class SelectedCollections(
        override val name: String,
        val collectionIds: List<String>
    ) : ContractResource()

    data class SelectedVideos(
        override val name: String,
        val videoIds: List<String>
    ) : ContractResource()

    abstract val name: String
}
