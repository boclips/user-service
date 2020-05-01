package com.boclips.users.domain.service.user

import com.boclips.users.domain.model.user.Identity
import com.boclips.users.domain.model.user.UserId

interface IdentityProvider {
    fun getIdentitiesById(id: UserId): Identity?
    fun getIdentity(): Sequence<Identity>
    fun createIdentity(email: String, password: String, role: String?): Identity
    fun count(): Int
}
