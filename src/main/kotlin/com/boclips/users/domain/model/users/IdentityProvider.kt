package com.boclips.users.domain.model.users

import com.boclips.users.infrastructure.keycloakclient.KeycloakUser
import java.time.LocalDate

interface IdentityProvider {
    fun getUserById(id: String): KeycloakUser
    fun getUserByUsername(username: String): KeycloakUser
    fun hasLoggedIn(id: String): Boolean
    fun createUser(user: KeycloakUser): KeycloakUser
    fun deleteUserById(id: String): KeycloakUser
    fun getLastLoginUserIds(client: String, since: LocalDate): List<String>
}
