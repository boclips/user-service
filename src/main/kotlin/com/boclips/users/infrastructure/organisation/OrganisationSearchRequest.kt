package com.boclips.users.infrastructure.organisation

data class OrganisationSearchRequest(
    val countryCode: String,
    val page: Int?,
    val size: Int?
)
