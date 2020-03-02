package com.boclips.users.domain.service

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import org.springframework.data.domain.Page
import java.time.ZonedDateTime
import java.util.Collections.emptyList

interface OrganisationRepository {
    fun save(
        apiIntegration: ApiIntegration,
        accessRuleIds: List<AccessRuleId> = emptyList(),
        role: String? = null
    ): Organisation<ApiIntegration>

    fun save(
        school: School,
        accessExpiresOn: ZonedDateTime? = null
    ): Organisation<School>

    fun save(
        district: District,
        accessExpiresOn: ZonedDateTime? = null
    ): Organisation<District>

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