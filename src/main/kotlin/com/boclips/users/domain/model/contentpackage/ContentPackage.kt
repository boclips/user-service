package com.boclips.users.domain.model.contentpackage

data class ContentPackage (
    val id: String,
    val name: String,
    val accessRules: List<AccessRule>
)
