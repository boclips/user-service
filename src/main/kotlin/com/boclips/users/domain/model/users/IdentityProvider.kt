package com.boclips.users.domain.model.users

import java.time.LocalDate

interface IdentityProvider {
    companion object {
        const val TEACHERS_GROUP_NAME: String = "teachers"
    }

    fun getUserById(id: String): UserIdentity
    fun getUserByUsername(username: String): UserIdentity
    fun hasLoggedIn(id: String): Boolean
    fun getNewTeachers(since: LocalDate): List<String>
    fun getUsers(): List<UserIdentity>
}
