package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.VideoId
import org.bson.types.ObjectId

class AccessRuleFactory {
    companion object {
        fun sampleSelectedCollectionsAccessRule(
            id: AccessRuleId = AccessRuleId(ObjectId().toHexString()),
            name: String = "Tailored collections list",
            collectionIds: List<CollectionId> = emptyList()
        ) = AccessRule.SelectedCollections(id, name, collectionIds)

        fun sampleSelectedVideosAccessRule(
            id: AccessRuleId = AccessRuleId(ObjectId().toHexString()),
            name: String = "Tailored videos list",
            videoIds: List<VideoId> = emptyList()
        ) = AccessRule.SelectedVideos(id, name, videoIds)
    }
}
