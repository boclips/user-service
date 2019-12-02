package com.boclips.users.presentation.resources

import com.boclips.users.presentation.resources.school.CountryResource
import com.boclips.users.presentation.resources.school.StateResource

data class OrganisationResource(
    val name: String,
    val state: StateResource?,
    val country: CountryResource?
)
