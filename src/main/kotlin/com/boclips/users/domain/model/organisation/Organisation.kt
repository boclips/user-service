package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State

data class OrganisationAccount(
    val id: OrganisationAccountId,
    val contractIds: List<ContractId>,
    val organisation: Organisation
)

sealed class Organisation(
    open val name: String,
    open val country: Country? = null,
    open val state: State? = null
)

data class School(
    override val name: String,
    override val country: Country,
    override val state: State? = null,
    val district: District?,
    val externalId: String?
) : Organisation(
    name = name,
    country = country,
    state = state
)

data class District(
    override val name: String,
    override val state: State,
    val externalId: String,
    val schools: List<School> = emptyList()
) : Organisation(
    name = name,
    country = Country.usa(),
    state = state
)

data class ApiIntegration(
    override val name: String,
    override val country: Country? = null,
    override val state: State? = null
) : Organisation(
    name = name,
    country = country,
    state = state
)

