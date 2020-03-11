package com.boclips.users.infrastructure.accessrules

import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "accessRules")
sealed class AccessRuleDocument {
    @TypeAlias("IncludedCollections")
    class IncludedCollections : AccessRuleDocument() {
        lateinit var collectionIds: List<String>
    }

    @TypeAlias("IncludedVideos")
    class IncludedVideos : AccessRuleDocument() {
        lateinit var videoIds: List<String>
    }

    @TypeAlias("ExcludedVideos")
    class ExcludedVideos : AccessRuleDocument() {
        lateinit var videoIds: List<String>
    }

    @TypeAlias("ExcludedVideoTypes")
    class ExcludedVideoTypes : AccessRuleDocument() {
        lateinit var videoTypes: List<VideoTypeDocument>
    }

    lateinit var id: ObjectId
    lateinit var name: String
}
