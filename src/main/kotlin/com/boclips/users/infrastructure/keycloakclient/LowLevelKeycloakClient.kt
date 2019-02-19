package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.identity.IdentityId

interface LowLevelKeycloakClient {
    fun deleteUserById(id: IdentityId): Identity
    fun createUser(user: Identity): Identity
    fun createGroup(keycloakGroup: KeycloakGroup): KeycloakGroup
    fun addUserToGroup(userId: String, groupId: String)
}