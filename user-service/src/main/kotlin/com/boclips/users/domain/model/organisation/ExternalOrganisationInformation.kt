package com.boclips.users.domain.model.organisation

data class ExternalOrganisationInformation(
    val id: ExternalOrganisationId,
    val name: String,
    val address: Address
)
