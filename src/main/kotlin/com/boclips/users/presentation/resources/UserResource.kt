package com.boclips.users.presentation.resources

open class UserResource(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val analyticsId: String?
)