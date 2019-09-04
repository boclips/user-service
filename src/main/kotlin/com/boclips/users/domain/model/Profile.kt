package com.boclips.users.domain.model

import com.boclips.users.domain.model.school.Country

data class Profile(
    val firstName: String,
    val lastName: String,
    val subjects: List<Subject> = emptyList(),
    val ages: List<Int> = emptyList(),
    val hasOptedIntoMarketing: Boolean = false,
    val country: Country? = null
)

fun Profile?.getSubjects() = this?.subjects ?: emptyList()
fun Profile?.getAges() = this?.ages ?: emptyList()
