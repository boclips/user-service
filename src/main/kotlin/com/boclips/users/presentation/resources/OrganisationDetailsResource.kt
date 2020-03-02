package com.boclips.users.presentation.resources

import com.boclips.users.presentation.resources.school.CountryResource
import com.boclips.users.presentation.resources.school.StateResource

data class OrganisationDetailsResource(
    val name: String,
    val type: String?,
    val state: StateResource?,
    val country: CountryResource?,
    val allowsOverridingUserIds: Boolean?
)
