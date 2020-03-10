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
            is AccessRuleDocument.IncludedCollections -> AccessRule.IncludedCollections(
                id = AccessRuleId(document.id.toHexString()),
                name = document.name,
                collectionIds = document.collectionIds.map { CollectionId(it) }
            )
            is AccessRuleDocument.IncludedVideos -> AccessRule.IncludedVideos(
                id = AccessRuleId(document.id.toHexString()),
                name = document.name,
                videoIds = document.videoIds.map { VideoId(it) }
            )
            is AccessRuleDocument.ExcludedVideos -> AccessRule.ExcludedVideos(
                id = AccessRuleId(document.id.toHexString()),
                name = document.name,
                videoIds = document.videoIds.map { VideoId(it) }
            )
        }
    }

    fun toDocument(accessRule: AccessRule): AccessRuleDocument {
        return when (accessRule) {
            is AccessRule.IncludedCollections -> AccessRuleDocument.IncludedCollections().apply {
                id = ObjectId(accessRule.id.value)
                name = accessRule.name
                collectionIds = accessRule.collectionIds.map { it.value }
            }
            is AccessRule.IncludedVideos -> AccessRuleDocument.IncludedVideos().apply {
                id = ObjectId(accessRule.id.value)
                name = accessRule.name
                videoIds = accessRule.videoIds.map { it.value }
            }
            is AccessRule.ExcludedVideos -> AccessRuleDocument.ExcludedVideos().apply {
                id = ObjectId(accessRule.id.value)
                name = accessRule.name
                videoIds = accessRule.videoIds.map { it.value }
            }
        }
    }
}
