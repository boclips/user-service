package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.User
import org.springframework.stereotype.Component

@Component
class UserConverter {
    fun toUserResource(user: User): UserResource {
        return UserResource(
            id = user.userId.value,
            firstName = user.account.firstName,
            lastName = user.account.lastName,
            email = user.account.email,
            analyticsId = user.account.analyticsId?.let { it.value }
        )
    }
}
