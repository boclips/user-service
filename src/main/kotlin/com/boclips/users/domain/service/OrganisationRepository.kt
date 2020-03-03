package com.boclips.users.domain.service

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationDetails
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.School
import org.springframework.data.domain.Page

interface OrganisationRepository {
    fun <T: OrganisationDetails> save(organisation: Organisation<T>): Organisation<T>

    fun update(update: OrganisationUpdate): Organisation<*>?

    fun findOrganisationsByParentId(parentId: OrganisationId): List<Organisation<*>>
    fun findOrganisationById(id: OrganisationId): Organisation<*>?
    fun findOrganisationByExternalId(id: String): Organisation<*>?
    fun findOrganisations(
        countryCode: String?,
        types: List<OrganisationType>?,
        page: Int,
        size: Int
    ): Page<Organisation<*>>?

    fun findApiIntegrationByRole(role: String): Organisation<ApiIntegration>?
    fun findApiIntegrationByName(name: String): Organisation<ApiIntegration>?

    fun lookupSchools(schoolName: String, countryCode: String) : List<LookupEntry>
    fun findSchoolById(id: OrganisationId): Organisation<School>?
    fun findSchools(): List<Organisation<School>>
}
