package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.UserIdentity

interface LowLevelKeycloakClient {
    fun deleteUserById(id: String): UserIdentity
    fun createUser(user: UserIdentity): UserIdentity
    fun createGroup(keycloakGroup: KeycloakGroup): KeycloakGroup
    fun addUserToGroup(userId: String, groupId: String)
}