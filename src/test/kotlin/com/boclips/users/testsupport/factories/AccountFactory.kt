package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.UserId

class AccountFactory {
    companion object {
        fun sample(
            id: String = "user-id",
            username: String = "joe@dough.com",
            roles: List<String> = emptyList()
        ) = Account(
            id = UserId(value = id),
            username = username,
            roles = roles
        )
    }
}
