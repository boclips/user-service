package com.boclips.users.domain.service

import com.boclips.users.domain.model.users.Identity
import java.time.LocalDate

interface IdentityProvider {
    fun getUserById(id: String): Identity?
    fun getUserByUsername(username: String): Identity
    fun hasLoggedIn(id: String): Boolean
    fun getNewTeachers(since: LocalDate): List<Identity>
    fun getUsers(): List<Identity>
}
