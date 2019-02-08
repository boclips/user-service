package com.boclips.users.testsupport

import com.boclips.users.domain.model.users.User

class UserFactory {
    companion object {
        fun sample(
            id: String = "user-id",
            activated: Boolean = false

        ) = User(
            id = id,
            activated = activated
        )
    }
}
