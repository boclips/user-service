package com.boclips.users.domain.service

import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import java.util.Collections.emptyList

interface OrganisationRepository {
    fun save(
        organisationName: String,
        role: String? = null,
        contractIds: List<ContractId> = emptyList(),
        districtId: String? = null,
        organisationType: OrganisationType?,
        countryId: String? = null,
        stateId: String? = null
    ): Organisation

    fun findByRole(role: String): Organisation?
    fun findById(id: OrganisationId): Organisation?
    fun findByDistrictId(districtId: String): Organisation?
    fun findByType(organisationType: OrganisationType): List<Organisation>
}
