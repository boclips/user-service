package com.boclips.users.testsupport

import com.boclips.users.domain.model.users.User
import com.boclips.users.domain.model.users.UserIdentity
import java.util.*

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

class UserIdentityFactory {
    companion object {
        fun sample(
                id: String = UUID.randomUUID().toString(),
                email: String = "test@test.com",
                firstName: String = "Test",
                lastName: String = "Test",
                isVerified: Boolean = false
        ) = UserIdentity(
                id = id,
                email = email,
                firstName = firstName,
                lastName = lastName,
                isVerified = isVerified
        )
    }
}
