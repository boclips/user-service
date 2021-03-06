package com.boclips.users.domain.model.access

import java.util.Locale

sealed class AccessRule {
    data class IncludedCollections(
        override val name: String,
        val collectionIds: List<CollectionId>
    ) : AccessRule()

    data class IncludedVideos(
        override val name: String,
        val videoIds: List<VideoId>
    ) : AccessRule()

    data class IncludedDistributionMethods(
        override val name: String,
        val distributionMethods: Set<DistributionMethod>
    ) : AccessRule()

    data class ExcludedVideos(
        override val name: String,
        val videoIds: List<VideoId>
    ) : AccessRule()

    data class ExcludedVideoTypes(
        override val name: String,
        val videoTypes: List<VideoType>
    ) : AccessRule()

    data class IncludedVideoTypes(
        override val name: String,
        val videoTypes: List<VideoType>
    ) : AccessRule()

    data class ExcludedChannels(
        override val name: String,
        val channelIds: List<ChannelId>
    ) : AccessRule()

    data class IncludedChannels(
        override val name: String,
        val channelIds: List<ChannelId>
    ) : AccessRule()

    data class IncludedVideoVoiceTypes(
        override val name: String,
        val voiceTypes: List<VideoVoiceType>
    ) : AccessRule()

    data class ExcludedLanguages(
        override val name: String,
        val languages: Set<Locale>
    ) : AccessRule()

    data class ExcludedPlaybackSources(
        override val name: String,
        val sources: Set<PlaybackSource>
    ) : AccessRule()

    data class IncludedPrivateChannels(
        override val name: String,
        val channelIds: List<ChannelId>
    ): AccessRule()

    abstract val name: String
}
