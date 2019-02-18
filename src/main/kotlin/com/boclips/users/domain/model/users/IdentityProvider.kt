package com.boclips.users.domain.model.users

import com.boclips.users.infrastructure.keycloakclient.KeycloakUser
import java.time.LocalDate

interface IdentityProvider {
    companion object {
        const val TEACHERS_GROUP_NAME: String = "teachers"
    }

    fun getUserById(id: String): KeycloakUser
    fun getUserByUsername(username: String): KeycloakUser
    fun hasLoggedIn(id: String): Boolean
    fun getNewTeachers(since: LocalDate): List<String>
    fun getUsers(): List<KeycloakUser>
}
