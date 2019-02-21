package com.boclips.users.domain.service

import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.identity.IdentityId
import java.time.LocalDate

interface IdentityProvider {
    fun getUserById(id: IdentityId): Identity?
    fun getUserByUsername(username: String): Identity
    fun getNewTeachers(since: LocalDate): List<Identity>
    fun getUsers(): List<Identity>
}
