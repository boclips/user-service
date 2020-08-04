package com.boclips.users.domain.model.access

sealed class AccessRule {
    data class IncludedCollections(
        override val id: AccessRuleId,
        override val name: String,
        val collectionIds: List<CollectionId>
    ) : AccessRule()

    data class IncludedVideos(
        override val id: AccessRuleId,
        override val name: String,
        val videoIds: List<VideoId>
    ) : AccessRule()

    data class IncludedDistributionMethods(
        override val id: AccessRuleId,
        override val name: String,
        val distributionMethods: Set<DistributionMethod>
    ) : AccessRule()

    data class ExcludedVideos(
        override val id: AccessRuleId,
        override val name: String,
        val videoIds: List<VideoId>
    ) : AccessRule()

    data class ExcludedVideoTypes(
        override val id: AccessRuleId,
        override val name: String,
        val videoTypes: List<VideoType>
    ) : AccessRule()

    data class ExcludedChannels(
        override val id: AccessRuleId,
        override val name: String,
        val channelIds: List<ChannelId>
    ) : AccessRule()

    data class IncludedChannels(
        override val id: AccessRuleId,
        override val name: String,
        val channelIds: List<ChannelId>
    ) : AccessRule()

    data class IncludedVideoVoiceTypes(
        override val id: AccessRuleId,
        override val name: String,
        val voiceTypes: List<VideoVoiceType>

    ) : AccessRule()

    abstract val id: AccessRuleId
    abstract val name: String
}
