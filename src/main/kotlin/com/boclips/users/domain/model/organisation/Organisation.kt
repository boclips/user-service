package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State

data class OrganisationAccount<T: Organisation>(
    val id: OrganisationAccountId,
    val contractIds: List<ContractId>,
    val organisation: T
)

enum class OrganisationType {
    API, SCHOOL, DISTRICT
}

sealed class Organisation(
    open val name: String,
    open val country: Country? = null,
    open val state: State? = null,
    open val postcode: String? = null
) {
    abstract fun type(): OrganisationType
}

data class School(
    override val name: String,
    override val country: Country,
    override val state: State? = null,
    override val postcode: String? = null,
    val district: OrganisationAccount<District>?,
    val externalId: String?
) : Organisation(
    name = name,
    country = country,
    state = state
) {
    override fun type(): OrganisationType {
        return OrganisationType.SCHOOL
    }
}

data class District(
    override val name: String,
    override val state: State,
    val externalId: String
) : Organisation(
    name = name,
    country = Country.usa(),
    state = state
) {
    override fun type(): OrganisationType {
        return OrganisationType.DISTRICT
    }
}

data class ApiIntegration(
    override val name: String,
    override val country: Country? = null,
    override val state: State? = null
) : Organisation(
    name = name,
    country = country,
    state = state
) {
    override fun type(): OrganisationType {
        return OrganisationType.API
    }
}

