package com.boclips.users.domain.model.users

import java.time.LocalDateTime

interface IdentityProvider {
    companion object {
        const val TEACHERS_GROUP_NAME: String = "teachers"
    }

    fun getUserById(keycloakId: KeycloakId): User
    fun hasLoggedIn(keycloakId: KeycloakId): Boolean
    fun createUserIfDoesntExist(user: User): User
    fun deleteUserById(keycloakId: KeycloakId): User
    fun getUsersRegisteredSince(since: LocalDateTime): List<User>
    fun getUserIdsRegisteredSince(since: LocalDateTime): List<KeycloakId>
    fun getUsers(): List<User>
}
