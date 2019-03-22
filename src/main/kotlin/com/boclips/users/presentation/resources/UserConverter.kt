package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.User

class UserConverter {
    fun toUserResource(user: User): UserResource {
        return UserResource(
            id = user.userId.value,
            firstName = user.identity.firstName,
            lastName = user.identity.lastName,
            email = user.identity.email,
            analyticsId = user.account.analyticsId?.let { it.value }
        )
    }
}
