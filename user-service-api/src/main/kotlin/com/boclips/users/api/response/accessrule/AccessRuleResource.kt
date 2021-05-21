package com.boclips.users.api.response.accessrule

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, visible = true)
sealed class AccessRuleResource(val type: String) {
    data class IncludedCollections(
        override val name: String,
        val collectionIds: List<String>
    ) : AccessRuleResource(type = "IncludedCollections")

    data class IncludedVideos(
        override val name: String,
        val videoIds: List<String>
    ) : AccessRuleResource(type = "IncludedVideos")

    data class IncludedDistributionMethods(
        override val name: String,
        val distributionMethods: List<String>
    ) : AccessRuleResource(type = "IncludedDistributionMethods")

    data class ExcludedVideos(
        override val name: String,
        val videoIds: List<String>
    ) : AccessRuleResource(type = "ExcludedVideos")

    data class ExcludedVideoTypes(
        override val name: String,
        val videoTypes: List<String>
    ) : AccessRuleResource(type = "ExcludedVideoTypes")

    data class IncludedVideoTypes(
        override val name: String,
        val videoTypes: List<String>
    ) : AccessRuleResource(type = "IncludedVideoTypes")

    data class ExcludedChannels(
        override val name: String,
        val channelIds: List<String>
    ) : AccessRuleResource(type = "ExcludedChannels")

    data class IncludedChannels(
        override val name: String,
        val channelIds: List<String>
    ) : AccessRuleResource(type = "IncludedChannel")

    data class IncludedVideoVoiceTypes(
        override val name: String,
        val voiceTypes: List<String>
    ) : AccessRuleResource(type = "IncludedVideoVoiceTypes")

    data class ExcludedLanguages(
        override val name: String,
        val languages: Set<String>
    ) : AccessRuleResource(type = "ExcludedLanguages")

    data class ExcludedPlaybackSources(
        override val name: String,
        val sources: Set<String>
    ) : AccessRuleResource(type = "ExcludedPlaybackSources")

    abstract val name: String
}
