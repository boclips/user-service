package com.boclips.users.testsupport

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.identity.Identity

class UserFactory {
    companion object {
        fun sample(
            userId: UserId = UserId(value = "user-id"),
            account: Account = AccountFactory.sample(),
            identity: Identity = UserIdentityFactory.sample()
        ) = User(
            account = account,
            userId = userId,
            identity = identity
        )
    }
}
