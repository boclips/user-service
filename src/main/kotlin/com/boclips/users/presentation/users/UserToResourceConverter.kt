package com.boclips.users.presentation.users

import com.boclips.users.domain.model.users.User

class UserToResourceConverter {
    companion object {
        fun convert(users: List<User>): List<UserResource> {
            return users.map { convert(it) }
        }

        fun convert(user: User): UserResource {
            return UserResource(
                    id = user.id,
                    activated = user.activated
            )
        }
    }
}