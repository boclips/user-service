package com.boclips.users.infrastructure.user

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "organisations")
data class OrganisationDocument(
    @BsonId
    val id: ObjectId,
    val name: String
)
