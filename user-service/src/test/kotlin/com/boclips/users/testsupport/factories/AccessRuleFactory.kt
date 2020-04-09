package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.ContentPartnerId
import com.boclips.users.domain.model.contentpackage.DistributionMethod
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.domain.model.contentpackage.VideoType

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
            contentPartnerIds: List<ContentPartnerId> = emptyList()
        ) = AccessRule.ExcludedContentPartners(id, name, contentPartnerIds)

        fun sampleIncludedDistributionMethodAccessRule(
            id: AccessRuleId = AccessRuleId(),
            name: String = "Included Distribution Methods",
            distributionMethods: Set<DistributionMethod>
        ): AccessRule.IncludedDistributionMethods =
            AccessRule.IncludedDistributionMethods(id, name, distributionMethods)
    }
}
