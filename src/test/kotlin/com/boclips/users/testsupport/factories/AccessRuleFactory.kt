package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.VideoId
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
    }
}
