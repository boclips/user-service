package com.boclips.users.testsupport

import com.boclips.users.domain.model.User

class UserFactory {
    companion object {
        fun sample(
            user: User = AccountFactory.sample(id = "user-id")
        ) = user
    }
}
