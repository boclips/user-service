package com.boclips.users.api.response.user

data class UsersResource(
    val _embedded: UserResourceWrapper
)

data class UserResourceWrapper(
    val users: List<UserResource>
)
