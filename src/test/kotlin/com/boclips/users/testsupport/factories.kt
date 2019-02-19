package com.boclips.users.testsupport

import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.model.account.Account
import java.util.UUID

class UserFactory {
    companion object {
        fun sample(
            id: String = "user-id",
            activated: Boolean = false

        ) = Account(
            id = id,
            activated = activated
        )
    }
}

class UserIdentityFactory {
    companion object {
        fun sample(
            id: String = UUID.randomUUID().toString(),
            email: String = "test@test.com",
            firstName: String = "Test",
            lastName: String = "Test",
            isVerified: Boolean = false
        ) = Identity(
            id = IdentityId(value = id),
            email = email,
            firstName = firstName,
            lastName = lastName,
            isVerified = isVerified
        )
    }
}
