package com.boclips.users.presentation.resources

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.hateoas.Link

sealed class AccessRuleResource(val type: String) {
    data class IncludedCollections(
        override val name: String,
        val collectionIds: List<String>,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        override val _links: Map<String, Link> = emptyMap()
    ) : AccessRuleResource(type = "IncludedCollections")

    data class IncludedVideos(
        override val name: String,
        val videoIds: List<String>,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        override val _links: Map<String, Link> = emptyMap()
    ) : AccessRuleResource(type = "IncludedVideos")

    data class ExcludedVideos(
        override val name: String,
        val videoIds: List<String>,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        override val _links: Map<String, Link> = emptyMap()
    ) : AccessRuleResource(type = "ExcludedVideos")

    data class ExcludedVideoTypes(
        override val name: String,
        val videoTypes: List<String>,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        override val _links: Map<String, Link> = emptyMap()
    ) : AccessRuleResource(type = "ExcludedVideoTypes")

    data class ExcludedContentPartners(
        override val name: String,
        val contentPartnerIds: List<String>,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        override val _links: Map<String, Link>
    ) : AccessRuleResource(type = "ExcludedContentPartners")

    abstract val name: String
    abstract val _links: Map<String, Link>
}
