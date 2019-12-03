package com.boclips.users.presentation.resources

import java.time.ZonedDateTime

data class OrganisationAccountResource(
    val name: String,
    val contractIds: List<String>,
    val accessExpiresOn: ZonedDateTime?,
    val type: String?,
    val organisation: OrganisationResource
)
