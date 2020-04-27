package com.boclips.users.domain.service

import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.Page
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.OrganisationType

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

    fun findApiIntegrationByRole(role: String): ApiIntegration?
    fun findApiIntegrationByName(name: String): ApiIntegration?

    fun lookupSchools(schoolName: String, countryCode: String): List<School>
    fun findSchoolById(id: OrganisationId): School?
    fun findSchools(): List<School>
}
