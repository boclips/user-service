package com.boclips.users.domain.service

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.organisation.School
import java.util.Collections.emptyList

interface OrganisationAccountRepository {
    fun save(
        apiIntegration: ApiIntegration,
        contractIds: List<ContractId> = emptyList(),
        role: String? = null
    ): OrganisationAccount<ApiIntegration>

    fun save(
        school: School
    ): OrganisationAccount<School>

    fun save(
        district: District
    ): OrganisationAccount<District>

    fun findOrganisationAccountById(id: OrganisationAccountId): OrganisationAccount<*>?
    fun findOrganisationAccountByExternalId(id: String): OrganisationAccount<*>?


    fun findApiIntegrationByRole(role: String): OrganisationAccount<ApiIntegration>?
    fun findApiIntegrationByName(name: String): OrganisationAccount<ApiIntegration>?

    fun lookupSchools(schoolName: String, countryCode: String) : List<LookupEntry>
    fun findSchoolById(id: OrganisationAccountId): OrganisationAccount<School>?
    fun findSchools(): List<OrganisationAccount<School>>
}
