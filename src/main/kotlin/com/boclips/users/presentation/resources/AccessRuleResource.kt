package com.boclips.users.presentation.resources

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.hateoas.Link

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = AccessRuleResource.IncludedCollections::class, name = "IncludedCollections"),
    JsonSubTypes.Type(value = AccessRuleResource.IncludedVideos::class, name = "IncludedVideos")
)
sealed class AccessRuleResource {
    data class IncludedCollections(
        override val name: String,
        val collectionIds: List<String>,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        override val _links: Map<String, Link> = emptyMap()
    ) : AccessRuleResource()

    data class IncludedVideos(
        override val name: String,
        val videoIds: List<String>,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        override val _links: Map<String, Link> = emptyMap()
    ) : AccessRuleResource()

    abstract val name: String
    abstract val _links: Map<String, Link>
}
