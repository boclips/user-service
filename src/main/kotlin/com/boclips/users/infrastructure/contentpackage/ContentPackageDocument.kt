package com.boclips.users.infrastructure.contentpackage

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "contentPackages")
class ContentPackageDocument(
    @Id
    val id: ObjectId,
    val name: String,
    val accessRuleIds: List<String>
)
