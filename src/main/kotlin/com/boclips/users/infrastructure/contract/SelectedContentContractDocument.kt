package com.boclips.users.infrastructure.contract

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "contracts")
data class SelectedContentContractDocument(
    @BsonId
    val id: ObjectId,
    val name: String,
    val collectionIds: List<String> = emptyList()
)
