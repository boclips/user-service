package com.boclips.users.presentation.resources

import com.boclips.users.api.response.user.UserResource

data class UsersResource(
    val _embedded: UserResourceWrapper
)

data class UserResourceWrapper(
    val users: List<UserResource>
)
