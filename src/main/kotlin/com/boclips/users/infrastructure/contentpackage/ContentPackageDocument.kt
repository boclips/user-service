package com.boclips.users.infrastructure.contentpackage

import com.boclips.users.infrastructure.accessrules.AccessRuleDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "contentPackages")
class ContentPackageDocument(
    @Id
    val id: ObjectId,
    val name: String,
    val accessRules: List<AccessRuleDocument>
)
