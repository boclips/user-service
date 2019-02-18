package com.boclips.users.infrastructure.keycloakclient

interface LowLevelKeycloakClient {
    fun deleteUserById(id: String): KeycloakUser
    fun createUser(user: KeycloakUser): KeycloakUser
    fun createGroup(keycloakGroup: KeycloakGroup): KeycloakGroup
    fun addUserToGroup(userId: String, groupId: String)
}