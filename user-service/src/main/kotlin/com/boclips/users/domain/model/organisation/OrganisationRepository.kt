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
        size: Int,
        hasCustomPrices: Boolean? = null
    ): Page<Organisation>

    fun findByRoleIn(roles: List<String>): List<Organisation>
    fun findByTag(tag: OrganisationTag): List<Organisation>
    fun findByEmailDomain(domain: String): List<Organisation>
    fun findApiIntegrationByName(name: String): ApiIntegration?
    fun findDistrictByName(name: String): District?

    fun lookupSchools(schoolName: String, countryCode: String): List<School>
    fun findSchoolById(id: OrganisationId): School?
}
