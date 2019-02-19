package com.boclips.users.domain.model.identity

data class Identity(
    val id: IdentityId,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isVerified: Boolean
) {
    override fun toString(): String {
        return "Identity(id=$id, isVerified=$isVerified)"
    }
}