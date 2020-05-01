package com.boclips.users.domain.model.user

import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.subject.Subject

data class Profile(
    val firstName: String,
    val lastName: String,
    val subjects: List<Subject> = emptyList(),
    val ages: List<Int> = emptyList(),
    val hasOptedIntoMarketing: Boolean = false,
    val school: School? = null,
    val role: String? = null
)

fun Profile?.getSubjects() = this?.subjects ?: emptyList()
fun Profile?.getAges() = this?.ages ?: emptyList()
fun Profile?.getRole() = this?.role ?: ""
