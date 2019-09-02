package com.boclips.users.domain.model

import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State

data class Profile(
    val firstName: String,
    val lastName: String,
    val subjects: List<Subject> = emptyList(),
    val ages: List<Int> = emptyList(),
    val hasOptedIntoMarketing: Boolean = false,
    val country: Country? = null,
    val state: State? = null,
    val school: String = ""
)

fun Profile?.getSubjects() = this?.subjects ?: emptyList()
fun Profile?.getAges() = this?.ages ?: emptyList()
