package com.boclips.users.domain.model.organisation

data class ExternalSchoolInformation(
    val school: ExternalOrganisationInformation,
    val district: ExternalOrganisationInformation?
)
