package com.boclips.users.domain.service

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.accessrules.AccessRuleId
import com.boclips.users.domain.model.account.Organisation
import com.boclips.users.domain.model.account.OrganisationId
import com.boclips.users.domain.model.account.ApiIntegration
import com.boclips.users.domain.model.account.District
import com.boclips.users.domain.model.account.OrganisationType
import com.boclips.users.domain.model.account.School
import org.springframework.data.domain.Page
import java.time.ZonedDateTime
import java.util.Collections.emptyList

interface AccountRepository {
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

    fun update(update: AccountUpdate): Organisation<*>?

    fun findAccountsByParentId(parentId: OrganisationId): List<Organisation<*>>
    fun findAccountById(id: OrganisationId): Organisation<*>?
    fun findAccountByExternalId(id: String): Organisation<*>?
    fun findAccounts(
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
