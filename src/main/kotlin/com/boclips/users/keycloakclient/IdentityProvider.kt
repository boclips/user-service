package com.boclips.users.keycloakclient

import javax.ws.rs.core.Response

interface IdentityProvider {
    fun getUserById(id: String): KeycloakUser
    fun getUserByUsername(username: String): KeycloakUser
    fun hasLoggedIn(id: String): Boolean
    fun createUser(user: KeycloakUser): KeycloakUser
    fun deleteUserById(id: String): KeycloakUser
}
