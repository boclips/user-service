package com.boclips.users.infrastructure.organisation

import org.springframework.data.domain.Page

interface CustomizedOrganisationRepository {
    fun findOrganisations(searchRequest: OrganisationSearchRequest): Page<OrganisationDocument>
}
