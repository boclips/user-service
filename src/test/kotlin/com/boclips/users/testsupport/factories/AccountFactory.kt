package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.Platform

class AccountFactory {
    companion object {
        fun sample(
            id: String = "user-id",
            username: String = "joe@dough.com",
            platform: Platform = Platform.BoclipsForTeachers
        ) = Account(
            id = UserId(value = id),
            username = username,
            platform = platform
        )
    }
}
