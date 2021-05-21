package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.ChannelId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.model.access.DistributionMethod
import com.boclips.users.domain.model.access.PlaybackSource
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.domain.model.access.VideoType
import com.boclips.users.domain.model.access.VideoVoiceType
import java.util.Locale

class AccessRuleFactory {
    companion object {
        fun sampleIncludedCollectionsAccessRule(
            name: String = "Tailored collections list",
            collectionIds: List<CollectionId> = emptyList()
        ) = AccessRule.IncludedCollections(name, collectionIds)

        fun sampleIncludedVideosAccessRule(
            name: String = "Tailored videos list",
            videoIds: List<VideoId> = emptyList()
        ) = AccessRule.IncludedVideos(name, videoIds)

        fun sampleExcludedVideosAccessRule(
            name: String = "Tailored videos list",
            videoIds: List<VideoId> = emptyList()
        ) = AccessRule.ExcludedVideos(name, videoIds)

        fun sampleExcludedVideoTypesAccessRule(
            name: String = "Excluded Types",
            videoTypes: List<VideoType> = emptyList()
        ) = AccessRule.ExcludedVideoTypes(name, videoTypes)

        fun sampleIncludedVideoTypesAccessRule(
            name: String = "Included Types",
            videoTypes: List<VideoType> = emptyList()
        ) = AccessRule.IncludedVideoTypes(name, videoTypes)

        fun sampleExcludedContentPartnersAccessRule(
            name: String = "Excluded Content Partners",
            channelIds: List<ChannelId> = emptyList()
        ) = AccessRule.ExcludedChannels(name, channelIds)

        fun sampleIncludedChannelsAccessRule(
            name: String = "Included Channels",
            channelIds: List<ChannelId> = emptyList()
        ) = AccessRule.IncludedChannels(name, channelIds)

        fun sampleIncludedDistributionMethodsAccessRule(
            name: String = "Included Distribution Methods",
            distributionMethods: Set<DistributionMethod>
        ): AccessRule.IncludedDistributionMethods =
            AccessRule.IncludedDistributionMethods(name, distributionMethods)

        fun sampleIncludedVideoVoiceTypeAccessRule(
            name: String = "Included Video Voice Types",
            videoVoiceTypes: List<VideoVoiceType>
        ): AccessRule.IncludedVideoVoiceTypes =
            AccessRule.IncludedVideoVoiceTypes(name = name, voiceTypes = videoVoiceTypes)

        fun sampleExcludedLanguagesAccessRule(
            name: String = "Excluded Languages",
            languages: Set<Locale>
        ): AccessRule.ExcludedLanguages =
            AccessRule.ExcludedLanguages(name = name, languages = languages)

        fun sampleExcludedPlaybackSourcesAccessRule(
            name: String = "Excluded Playback Source",
            sources: Set<PlaybackSource>
        ): AccessRule.ExcludedPlaybackSources =
            AccessRule.ExcludedPlaybackSources(name = name, sources = sources)
    }
}
