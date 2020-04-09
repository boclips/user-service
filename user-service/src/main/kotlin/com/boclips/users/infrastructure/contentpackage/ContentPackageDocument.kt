package com.boclips.users.infrastructure.contentpackage

import org.bson.types.ObjectId

class ContentPackageDocument(
    val _id: ObjectId,
    val name: String,
    val accessRuleIds: List<String>
)
