package com.boclips.users.domain.service

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId

interface OrganisationRepository {
    fun save(organisationName: String, role: String? = null, contractIds: List<ContractId> = emptyList()): Organisation
    fun findByRole(role: String): Organisation?
    fun findById(id: OrganisationId): Organisation?
}
