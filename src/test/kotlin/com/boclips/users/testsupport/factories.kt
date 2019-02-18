package com.boclips.users.testsupport

import com.boclips.users.domain.model.users.User
import com.boclips.users.infrastructure.keycloakclient.KeycloakUser

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

class KeycloakUserFactory {
    companion object {
        fun sample(
            id: String? = null,
            email: String = "test@test.com",
            firstName: String? = "Test",
            lastName: String? = "Test",
            isVerified: Boolean = true
        ) = KeycloakUser(
            username = email,
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            isVerified = isVerified
        )
    }
}
