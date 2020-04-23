package com.boclips.users.api.response.organisation

import com.boclips.users.api.response.country.CountryResource
import com.boclips.users.api.response.state.StateResource

data class OrganisationDetailsResource(
    val id: String,
    val name: String,
    val domain: String?,
    val type: String?,
    val state: StateResource?,
    val country: CountryResource?,
    val allowsOverridingUserIds: Boolean?
)
