package com.boclips.users.infrastructure.keycloak.client

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.UserId
import org.keycloak.representations.idm.UserRepresentation

class KeycloakUserToAccountConverter {
    fun convert(userRepresentation: UserRepresentation): Account {
        val userId = userRepresentation.id

        check(userId.isNotEmpty())

        return Account(
            id = UserId(value = userId),
            username = userRepresentation.username,
            roles = userRepresentation.realmRoles
        )
    }
}
