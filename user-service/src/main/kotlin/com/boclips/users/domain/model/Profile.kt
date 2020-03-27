package com.boclips.users.domain.model

data class Profile(
    val firstName: String,
    val lastName: String,
    val subjects: List<Subject> = emptyList(),
    val ages: List<Int> = emptyList(),
    val hasOptedIntoMarketing: Boolean = false,
    val role: String? = null
)

fun Profile?.getSubjects() = this?.subjects ?: emptyList()
fun Profile?.getAges() = this?.ages ?: emptyList()
