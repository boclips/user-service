package com.boclips.users.domain.model

data class UpdatedUser(
    val userId: UserId,
    val firstName: String,
    val lastName: String,
    val subjects: List<Subject>,
    val ages: List<Int>,
    val hasOptedIntoMarketing: Boolean
)
