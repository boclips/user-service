package com.boclips.users.domain.service

import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.UserId

interface IdentityProvider {
    fun getIdentitiesById(id: UserId): Identity?
    fun getIdentity(): Sequence<Identity>
    fun createIdentity(email: String, password: String, role: String?): Identity
    fun count(): Int
}
