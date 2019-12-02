package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import java.time.ZonedDateTime

data class OrganisationAccount<T: Organisation>(
    val id: OrganisationAccountId,
    val type: OrganisationAccountType,
    val contractIds: List<ContractId>,
    val organisation: T
)

enum class OrganisationAccountType {
    DESIGN_PARTNER,
    STANDARD
}

enum class OrganisationType {
    API, SCHOOL, DISTRICT
}

sealed class Organisation(
    open val name: String,
    open val country: Country? = null,
    open val state: State? = null,
    open val postcode: String? = null,
    open val accessExpiresOn: ZonedDateTime? = null
) {
    abstract fun type(): OrganisationType
}

data class School(
    override val name: String,
    override val country: Country,
    override val state: State? = null,
    override val postcode: String? = null,
    override val accessExpiresOn: ZonedDateTime? = null,
    val district: OrganisationAccount<District>?,
    val externalId: String?
) : Organisation(
    name = name,
    country = country,
    state = state,
    accessExpiresOn = accessExpiresOn
) {
    override fun type(): OrganisationType {
        return OrganisationType.SCHOOL
    }
}

data class District(
    override val name: String,
    override val state: State,
    override val accessExpiresOn: ZonedDateTime? = null,
    val externalId: String
) : Organisation(
    name = name,
    country = Country.usa(),
    state = state,
    accessExpiresOn = accessExpiresOn
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

