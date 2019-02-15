package com.boclips.users.testsupport

import com.boclips.users.domain.model.users.KeycloakId
import com.boclips.users.domain.model.users.User
import com.boclips.users.infrastructure.keycloakclient.KeycloakUser
import java.time.LocalDateTime

class UserFactory {
    companion object {
        fun sample(
            id: String = "user-id",
            activated: Boolean = false,
            firstName: String = "Matt",
            lastName: String = "Hello",
            email: String = "hello@boclips.com",
            createdAt: LocalDateTime = LocalDateTime.now(),
            mixpanelId: String? = null,
            subjects: String = "Maths,English  Sports"
        ) = User(
            keycloakId = KeycloakId(value = id),
            activated = activated,
            firstName = firstName,
            lastName = lastName,
            email = email,
            createdAt = createdAt,
            mixpanelId = mixpanelId,
            subjects = subjects
        )
    }
}

class KeycloakUserFactory {
    companion object {
        fun sample(
            id: String? = null,
            email: String = "test@test.com",
            firstName: String = "Test",
            lastName: String = "Test"
        ) = KeycloakUser(
            username = email,
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName
        )
    }
}

