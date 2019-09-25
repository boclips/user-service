package com.boclips.users.presentation.resources

import com.boclips.users.presentation.resources.school.CountryResource

open class UserResource(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val ages: List<Int>?,
    val subjects: List<String>?,
    val email: String?,
    val analyticsId: String?,
    val country: CountryResource?,
    val organisation: OrganisationResource?
)
