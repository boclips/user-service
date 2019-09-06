package com.boclips.users.domain.service

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.infrastructure.organisation.OrganisationType
import java.util.Collections.emptyList

interface OrganisationAccountRepository {
    fun save(
        role: String? = null,
        contractIds: List<ContractId> = emptyList(),
        apiIntegration: ApiIntegration
    ): OrganisationAccount

    fun save(
        school: School
    ): OrganisationAccount

    fun save(
        district: District
    ): OrganisationAccount

    fun findOrganisationAccountById(id: OrganisationAccountId): OrganisationAccount?

    fun findDistricts(): List<OrganisationAccount>

    fun findApiIntegrationByRole(role: String): OrganisationAccount?
    fun findApiIntegrationByName(name: String): OrganisationAccount?

    fun lookupSchools(organisationName: String, country: String) : List<LookupEntry>
}
