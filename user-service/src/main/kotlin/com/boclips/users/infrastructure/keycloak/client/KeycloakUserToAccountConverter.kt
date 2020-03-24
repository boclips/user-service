package com.boclips.users.infrastructure.keycloak.client

import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.UserId
import org.keycloak.representations.idm.UserRepresentation
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

class KeycloakUserToAccountConverter {
    fun convert(userRepresentation: UserRepresentation): Identity {
        val userId = userRepresentation.id

        check(userId.isNotEmpty())

        return Identity(
            id = UserId(value = userId),
            username = userRepresentation.username,
            roles = userRepresentation.realmRoles,
            createdAt = ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(userRepresentation.createdTimestamp),
                ZoneOffset.UTC
            )
        )
    }
}