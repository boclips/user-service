package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.model.access.ChannelId
import com.boclips.users.domain.model.access.DistributionMethod
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.domain.model.access.VideoType

class AccessRuleFactory {
    companion object {
        fun sampleIncludedCollectionsAccessRule(
            id: AccessRuleId = AccessRuleId(),
            name: String = "Tailored collections list",
            collectionIds: List<CollectionId> = emptyList()
        ) = AccessRule.IncludedCollections(id, name, collectionIds)

        fun sampleIncludedVideosAccessRule(
            id: AccessRuleId = AccessRuleId(),
            name: String = "Tailored videos list",
            videoIds: List<VideoId> = emptyList()
        ) = AccessRule.IncludedVideos(id, name, videoIds)

        fun sampleExcludedVideosAccessRule(
            id: AccessRuleId = AccessRuleId(),
            name: String = "Tailored videos list",
            videoIds: List<VideoId> = emptyList()
        ) = AccessRule.ExcludedVideos(id, name, videoIds)

        fun sampleExcludedVideoTypesAccessRule(
            id: AccessRuleId = AccessRuleId(),
            name: String = "Excluded Types",
            videoTypes: List<VideoType> = emptyList()
        ) = AccessRule.ExcludedVideoTypes(id, name, videoTypes)

        fun sampleExcludedContentPartnersAccessRule(
            id: AccessRuleId = AccessRuleId(),
            name: String = "Excluded Content Partners",
            channelIds: List<ChannelId> = emptyList()
        ) = AccessRule.ExcludedContentPartners(id, name, channelIds)

        fun sampleIncludedChannelsAccessRule(
            id: AccessRuleId = AccessRuleId(),
            name: String = "Included Channels",
            channelIds: List<ChannelId> = emptyList()
        ) = AccessRule.IncludedChannels(id, name, channelIds)

        fun sampleIncludedDistributionMethodAccessRule(
            id: AccessRuleId = AccessRuleId(),
            name: String = "Included Distribution Methods",
            distributionMethods: Set<DistributionMethod>
        ): AccessRule.IncludedDistributionMethods =
            AccessRule.IncludedDistributionMethods(id, name, distributionMethods)
    }
}
