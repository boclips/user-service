package com.boclips.users.domain.service.user

import com.boclips.users.domain.model.user.Identity
import com.boclips.users.domain.model.user.UserId

interface IdentityProvider {
    fun getIdentitiesById(id: UserId): Identity?
    fun getAllIdentityIds(): List<UserId>
    fun createIdentity(email: String, password: String, role: String?, isPasswordTemporary: Boolean): Identity
    fun count(): Int
    fun deleteIdentity(id: UserId)
}
