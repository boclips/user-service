package com.boclips.users.domain.model

class CrmProfile(
    val id: UserId,
    val activated: Boolean,
    val subjects: List<Subject>,
    val ageRange: List<Int>,
    val firstName: String,
    val lastName: String,
    val email: String,
    val hasOptedIntoMarketing: Boolean
) {
    override fun toString(): String {
        return "CrmProfile(id=$id)"
    }
}
