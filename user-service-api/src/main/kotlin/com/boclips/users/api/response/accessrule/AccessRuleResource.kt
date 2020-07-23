package com.boclips.users.api.response.accessrule

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, visible = true)
sealed class AccessRuleResource(val type: String) {
    data class IncludedCollections(
        override val id: String,
        override val name: String,
        val collectionIds: List<String>
    ) : AccessRuleResource(type = "IncludedCollections")

    data class IncludedVideos(
        override val id: String,
        override val name: String,
        val videoIds: List<String>
    ) : AccessRuleResource(type = "IncludedVideos")

    data class IncludedDistributionMethods(
        override val id: String,
        override val name: String,
        val distributionMethods: List<String>
    ) : AccessRuleResource(type = "IncludedDistributionMethods")

    data class ExcludedVideos(
        override val id: String,
        override val name: String,
        val videoIds: List<String>
    ) : AccessRuleResource(type = "ExcludedVideos")

    data class ExcludedVideoTypes(
        override val id: String,
        override val name: String,
        val videoTypes: List<String>
    ) : AccessRuleResource(type = "ExcludedVideoTypes")

    data class ExcludedChannels(
        override val id: String,
        override val name: String,
        val channelIds: List<String>
    ) : AccessRuleResource(type = "ExcludedChannels")

    data class IncludedChannels(
        override val id: String,
        override val name: String,
        val channelIds: List<String>
    ) : AccessRuleResource(type = "IncludedChannel")

    abstract val id: String
    abstract val name: String
}
