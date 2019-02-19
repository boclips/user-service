package com.boclips.users.domain.model.identity

data class Identity(
    val id: IdentityId,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isVerified: Boolean
)