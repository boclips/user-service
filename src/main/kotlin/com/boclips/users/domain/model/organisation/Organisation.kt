package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State

data class Organisation(
    val id: OrganisationId,
    val name: String,
    val country: Country? = null,
    val state: State? = null,
    val contractIds: List<ContractId>
)
