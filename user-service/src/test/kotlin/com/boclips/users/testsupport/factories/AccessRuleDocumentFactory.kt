package com.boclips.users.testsupport.factories

import com.boclips.users.infrastructure.accessrules.AccessRuleDocument
import org.bson.types.ObjectId

class AccessRuleDocumentFactory {
    companion object {
        fun sampleIncludedCollectionsAccessRuleDocument(
            name: String = "Test included collections access rule",
            collectionIds: List<String> = emptyList()
        ) = AccessRuleDocument.IncludedCollections().apply {
            this.id = ObjectId()
            this.name = name
            this.collectionIds = collectionIds
        }

        fun sampleIncludedVideosAccessRuleDocument(
            name: String = "Test included videos access rule",
            videoIds: List<String> = emptyList()
        ) = AccessRuleDocument.IncludedVideos().apply {
            this.id = ObjectId()
            this.name = name
            this.videoIds = videoIds
        }
    }
}
