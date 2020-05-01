package com.boclips.users.domain.model.access

data class ContentPackage(
    val id: ContentPackageId,
    val name: String,
    val accessRuleIds: List<AccessRuleId>
)

data class ContentPackageId(val value: String)
