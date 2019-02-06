package com.boclips.users.testsupport

import com.boclips.users.domain.model.users.User
import com.boclips.users.infrastructure.keycloakclient.KeycloakUser
import java.time.Instant
import java.util.*


object UserFactory {
    fun sample(
            id: String = "user-id",
            activated: Boolean = false,
            firstName: String = "Steve",
            lastName: String = "Jobs",
            email: String = "steve.jobs@boclips.com",
            createdDate: Date = Date.from(Instant.now()),
            mixpanelDistinctId: String = "always watching",
            subjects: List<String> = listOf("Apple", "Computers")
    ) = User(
            id = id,
            activated = activated,
            firstName = firstName,
            lastName = lastName,
            email = email,
            createdDate = createdDate,
            mixpanelDistinctId = mixpanelDistinctId,
            subjects = subjects
    )
}

object KeycloakUserFactory {
    fun sample(
            id: String = "user-id",
            emailVerified: Boolean = false,
            firstName: String = "Steve",
            lastName: String = "Jobs",
            email: String = "steve.jobs@boclips.com",
            mixpanelDistinctId: String = "always watching",
            subjects: String = "Apple",
            username: String = "username"
    ) = KeycloakUser(id = id,
            emailVerified = emailVerified,
            firstName = firstName,
            lastName = lastName,
            email = email,
            mixpanelDistinctId = mixpanelDistinctId,
            subjects = subjects,
            username = username,
            date = Date.from(Instant.now())
    )

}
