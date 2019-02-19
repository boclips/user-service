package com.boclips.users.domain.model.users

data class Identity(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isVerified: Boolean
)