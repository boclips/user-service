package com.boclips.users.api.response.accessrule

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.hateoas.Link

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, visible = true)
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

    data class IncludedDistributionMethod(
        override val name: String,
        val distributionMethods: List<String>,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        override val _links: Map<String, Link> = emptyMap()
    ) : AccessRuleResource(type = "IncludedDistributionMethods")

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

    data class ExcludedChannels(
        override val name: String,
        val channelIds: List<String>,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        override val _links: Map<String, Link> = emptyMap()
    ) : AccessRuleResource(type = "ExcludedChannels")

    data class IncludedChannels(
        override val name: String,
        val channelIds: List<String>,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        override val _links: Map<String, Link> = emptyMap()
    ) : AccessRuleResource(type = "IncludedChannel")

    abstract val name: String
    abstract val _links: Map<String, Link>
}
