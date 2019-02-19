package com.boclips.users.infrastructure.keycloak

import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.infrastructure.keycloak.client.KeycloakGroup

interface LowLevelKeycloakClient {
    fun deleteUserById(id: IdentityId): Identity
    fun createUser(user: Identity): Identity
    fun createGroup(keycloakGroup: KeycloakGroup): KeycloakGroup
    fun addUserToGroup(userId: String, groupId: String)
}