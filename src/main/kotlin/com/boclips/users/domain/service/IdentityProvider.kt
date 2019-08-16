package com.boclips.users.domain.service

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.identity.Identity

interface IdentityProvider {
    fun getUserById(id: UserId): Identity?
    fun getUsers(): List<Identity>
    fun createUser(email: String, password: String): Identity
    fun count(): Int
}
