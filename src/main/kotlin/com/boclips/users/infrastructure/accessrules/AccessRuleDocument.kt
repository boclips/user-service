package com.boclips.users.infrastructure.accessrules

import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "contracts")
sealed class AccessRuleDocument {
    @TypeAlias("SelectedCollections")
    class SelectedCollections : AccessRuleDocument() {
        lateinit var collectionIds: List<String>
    }

    @TypeAlias("SelectedVideos")
    class SelectedVideos : AccessRuleDocument() {
        lateinit var videoIds: List<String>
    }

    @TypeAlias("IncludedVideos")
    class IncludedVideos : AccessRuleDocument() {
        lateinit var videoIds: List<String>
    }

    lateinit var id: ObjectId
    lateinit var name: String
}
