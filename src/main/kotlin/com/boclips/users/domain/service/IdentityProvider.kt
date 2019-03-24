package com.boclips.users.domain.service

import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.UserId
import java.time.LocalDate

interface IdentityProvider {
    fun getUserById(id: UserId): Identity?
    fun getNewTeachers(since: LocalDate): List<Identity>
    fun getUsers(): List<Identity>
    fun createNewUser(firstName: String, lastName: String, email: String, password: String): Identity
}
