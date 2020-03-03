package com.boclips.users.domain.model.contentpackage

data class ContentPackage(
    val id: ContentPackageId,
    val name: String,
    val accessRules: List<AccessRule>
)

data class ContentPackageId(val value: String)
