package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.account.OrganisationType

data class OrganisationSearchRequest(

    val countryCode: String?,
    val organisationTypes: List<OrganisationType>?,
    val parentOnly: Boolean = false,
    val page: Int?,
    val size: Int?
)
