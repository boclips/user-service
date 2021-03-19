package com.boclips.users.infrastructure.keycloak.client

import com.boclips.users.domain.model.user.Identity
import com.boclips.users.domain.model.user.UserId
import org.keycloak.representations.idm.UserRepresentation
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

class KeycloakUserToAccountConverter {
    fun convert(userRepresentation: UserRepresentation): Identity {
        val userId = userRepresentation.id
        val userName = userRepresentation.username

        check(userId != null && userId.isNotEmpty())
        check(userName != null && userName.isNotEmpty())

        return Identity(
            id = UserId(value = userId),
            username = userName,
            idpEmail = userRepresentation.email,
            roles = userRepresentation.realmRoles,
            createdAt = ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(userRepresentation.createdTimestamp),
                ZoneOffset.UTC
            ),
            firstName = userRepresentation.firstName,
            lastName = userRepresentation.lastName
        )
    }
}
