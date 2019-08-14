package com.boclips.users.domain.model.identity

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSource

data class Identity(
    val id: UserId,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isVerified: Boolean,
    val associatedTo: UserSource
) {
    override fun toString(): String {
        return "Identity(id=$id, isVerified=$isVerified)"
    }
}
