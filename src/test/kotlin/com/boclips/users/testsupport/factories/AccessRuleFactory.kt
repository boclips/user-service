package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.ContentPartnerId
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.domain.model.contentpackage.VideoType
import org.bson.types.ObjectId

class AccessRuleFactory {
    companion object {
        fun sampleIncludedCollectionsAccessRule(
            id: AccessRuleId = AccessRuleId(ObjectId().toHexString()),
            name: String = "Tailored collections list",
            collectionIds: List<CollectionId> = emptyList()
        ) = AccessRule.IncludedCollections(id, name, collectionIds)

        fun sampleIncludedVideosAccessRule(
            id: AccessRuleId = AccessRuleId(ObjectId().toHexString()),
            name: String = "Tailored videos list",
            videoIds: List<VideoId> = emptyList()
        ) = AccessRule.IncludedVideos(id, name, videoIds)

        fun sampleExcludedVideosAccessRule(
            id: AccessRuleId = AccessRuleId(ObjectId().toHexString()),
            name: String = "Tailored videos list",
            videoIds: List<VideoId> = emptyList()
        ) = AccessRule.ExcludedVideos(id, name, videoIds)

        fun sampleExcludedVideoTypesAccessRule(
            id: AccessRuleId = AccessRuleId(ObjectId().toHexString()),
            name: String = "Excluded Types",
            videoTypes: List<VideoType> = emptyList()
        ) = AccessRule.ExcludedVideoTypes(id, name, videoTypes)

        fun sampleExcludedContentPartnersAccessRule(
            id: AccessRuleId = AccessRuleId(ObjectId().toHexString()),
            name: String = "Excluded Content Partners",
            contentPartnerIds: List<ContentPartnerId> = emptyList()
        ) = AccessRule.ExcludedContentPartners(id, name, contentPartnerIds)
    }
}
