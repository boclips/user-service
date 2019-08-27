package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.contract.ContractId

data class Organisation(
    val id: OrganisationId,
    val name: String,
    val contractIds: List<ContractId>
)
