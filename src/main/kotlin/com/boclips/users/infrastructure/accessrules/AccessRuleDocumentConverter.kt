package com.boclips.users.infrastructure.accessrules

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.VideoId
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class AccessRuleDocumentConverter {
    fun fromDocument(document: AccessRuleDocument): AccessRule {
        return when (document) {
            is AccessRuleDocument.SelectedCollections ->  AccessRule.SelectedCollections(
                id = AccessRuleId(document.id.toHexString()),
                name = document.name,
                collectionIds = document.collectionIds.map { CollectionId(it) }
            )
            is AccessRuleDocument.SelectedVideos -> AccessRule.SelectedVideos(
                id = AccessRuleId(document.id.toHexString()),
                name = document.name,
                videoIds = document.videoIds.map { VideoId(it) }
            )
        }
    }

    fun toDocument(accessRule: AccessRule): AccessRuleDocument {
        return when (accessRule) {
            is AccessRule.SelectedCollections -> AccessRuleDocument.SelectedCollections().apply {
                id = ObjectId(accessRule.id.value)
                name = accessRule.name
                collectionIds = accessRule.collectionIds.map { it.value }
            }
            is AccessRule.SelectedVideos -> AccessRuleDocument.SelectedVideos().apply {
                id = ObjectId(accessRule.id.value)
                name = accessRule.name
                videoIds = accessRule.videoIds.map { it.value }
            }
        }
    }
}
