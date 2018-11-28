package com.boclips.users.infrastructure.keycloakclient

interface IdentityProvider {
    fun getUserById(id: String): KeycloakUser
    fun getUserByUsername(username: String): KeycloakUser
    fun hasLoggedIn(id: String): Boolean
    fun createUser(user: KeycloakUser): KeycloakUser
    fun deleteUserById(id: String): KeycloakUser
}
