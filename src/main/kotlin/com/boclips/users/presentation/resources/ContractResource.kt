package com.boclips.users.presentation.resources

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.hateoas.Link

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = ContractResource.SelectedCollections::class, name = "SelectedCollections"),
    JsonSubTypes.Type(value = ContractResource.SelectedVideos::class, name = "SelectedVideos")
)
sealed class ContractResource {
    data class SelectedCollections(
        override val name: String,
        val collectionIds: List<String>,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        override val _links: Map<String, Link> = emptyMap()
    ) : ContractResource()

    data class SelectedVideos(
        override val name: String,
        val videoIds: List<String>,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        override val _links: Map<String, Link> = emptyMap()
    ) : ContractResource()

    abstract val name: String
    abstract val _links: Map<String, Link>
}
