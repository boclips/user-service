package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.Identity

interface LowLevelKeycloakClient {
    fun deleteUserById(id: String): Identity
    fun createUser(user: Identity): Identity
    fun createGroup(keycloakGroup: KeycloakGroup): KeycloakGroup
    fun addUserToGroup(userId: String, groupId: String)
}