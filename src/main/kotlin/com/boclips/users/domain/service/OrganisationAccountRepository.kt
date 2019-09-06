package com.boclips.users.domain.service

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.infrastructure.organisation.OrganisationType
import java.util.Collections.emptyList

interface OrganisationAccountRepository {
    fun save(
        role: String? = null,
        contractIds: List<ContractId> = emptyList(),
        organisation: Organisation
    ): OrganisationAccount

    fun findByRole(role: String): OrganisationAccount?
    fun findById(id: OrganisationAccountId): OrganisationAccount?
    fun findByDistrictId(districtId: String): OrganisationAccount?
    fun findDistricts(): List<OrganisationAccount>
    fun findByName(name: String): OrganisationAccount?

    fun lookupSchools(organisationName: String, country: String) : List<LookupEntry>
}
