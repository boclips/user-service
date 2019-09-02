package com.boclips.users.presentation.resources

import com.boclips.users.presentation.resources.school.CountryResource
import com.boclips.users.presentation.resources.school.StateResource

open class UserResource(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val ages: List<Int>?,
    val subjects: List<String>?,
    val country: CountryResource?,
    val state: StateResource?,
    val school: String?,
    val email: String?,
    val analyticsId: String?
)
