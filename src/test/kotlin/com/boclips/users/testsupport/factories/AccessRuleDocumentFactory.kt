package com.boclips.users.testsupport.factories

import com.boclips.users.infrastructure.accessrules.AccessRuleDocument
import org.bson.types.ObjectId

class AccessRuleDocumentFactory {
    companion object {
        fun sampleSelectedCollectionsAccessRuleDocument(
            name: String = "Test selected collections access rule",
            collectionIds: List<String> = emptyList()
        ) = AccessRuleDocument.SelectedCollections().apply {
            this.id = ObjectId()
            this.name = name
            this.collectionIds = collectionIds
        }

        fun sampleSelectedVideosAccessRuleDocument(
            name: String = "Test selected videos access rule",
            videoIds: List<String> = emptyList()
        ) = AccessRuleDocument.SelectedVideos().apply {
            this.id = ObjectId()
            this.name = name
            this.videoIds = videoIds
        }
    }
}
