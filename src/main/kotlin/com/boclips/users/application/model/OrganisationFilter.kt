package com.boclips.users.application.model

import com.boclips.users.domain.model.account.OrganisationType

data class OrganisationFilter(
    val id: String? = null,
    val countryCode: String? = null,
    val organisationTypes: List<OrganisationType>? = null,
    val parentOnly: Boolean = false,
    val page: Int,
    val size: Int
)
