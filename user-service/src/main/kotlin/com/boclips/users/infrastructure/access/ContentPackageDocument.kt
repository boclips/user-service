package com.boclips.users.infrastructure.access

import org.bson.types.ObjectId

class ContentPackageDocument(
    val _id: ObjectId,
    val name: String,
    val accessRuleIds: List<String>
)
