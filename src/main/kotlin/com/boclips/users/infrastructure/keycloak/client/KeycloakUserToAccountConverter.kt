package com.boclips.users.infrastructure.keycloak.client

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.UserId
import com.boclips.users.infrastructure.keycloak.UnknownUserSourceException
import com.boclips.users.infrastructure.organisation.UserSourceResolver
import org.keycloak.representations.idm.UserRepresentation

class KeycloakUserToAccountConverter(
    private val userSourceResolver: UserSourceResolver
) {

    fun convert(userRepresentation: UserRepresentation): Account {
        val userRole = userSourceResolver.resolve(userRepresentation.realmRoles)
            ?: throw UnknownUserSourceException("Could not resolve roles: ${userRepresentation.realmRoles}")
        val userId = userRepresentation.id

        if (userId.isEmpty()) throw IllegalStateException()

        return Account(
            id = UserId(value = userId),
            username = userRepresentation.username,
            organisationType = userRole
        )
    }
}
