package com.boclips.users.domain.model

import java.time.Instant

class CrmProfile(
    val id: UserId,
    val activated: Boolean,
    val subjects: List<Subject>,
    val ageRange: List<Int>,
    val firstName: String,
    val lastName: String,
    val email: String,
    val hasOptedIntoMarketing: Boolean,
    val lastLoggedIn: Instant?
) {
    override fun toString(): String {
        return "CrmProfile(id=$id)"
    }
}
