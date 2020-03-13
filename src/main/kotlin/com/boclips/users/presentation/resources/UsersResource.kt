package com.boclips.users.presentation.resources

data class UsersResource(
    val _embedded: UserResourceWrapper
)

data class UserResourceWrapper(
    val users: List<UserResource>
)
