package com.boclips.users.infrastructure.contract

import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "contracts")
sealed class ContractDocument {
    @TypeAlias("SelectedCollections")
    class SelectedCollections : ContractDocument() {
        lateinit var collectionIds: List<String>
    }

    @TypeAlias("SelectedVideos")
    class SelectedVideos : ContractDocument() {
        lateinit var videoIds: List<String>
    }

    lateinit var id: ObjectId
    lateinit var name: String
}
