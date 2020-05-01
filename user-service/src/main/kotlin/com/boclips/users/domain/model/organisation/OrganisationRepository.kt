package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.Page

interface OrganisationRepository {
    fun <T : Organisation> save(organisation: T): T

    fun update(id: OrganisationId, vararg updates: OrganisationUpdate): Organisation?

    fun findOrganisationsByParentId(parentId: OrganisationId): List<Organisation>
    fun findOrganisationById(id: OrganisationId): Organisation?
    fun findOrganisationByExternalId(id: ExternalOrganisationId): Organisation?
    fun findOrganisations(
        name: String? = null,
        countryCode: String? = null,
        types: List<OrganisationType>?,
        page: Int,
        size: Int
    ): Page<Organisation>

    fun findByRoleIn(roles: List<String>): List<Organisation>
    fun findApiIntegrationByName(name: String): ApiIntegration?

    fun lookupSchools(schoolName: String, countryCode: String): List<School>
    fun findSchoolById(id: OrganisationId): School?
}
