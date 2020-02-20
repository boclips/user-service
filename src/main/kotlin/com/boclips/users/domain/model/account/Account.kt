package com.boclips.users.domain.model.account

import com.boclips.users.domain.model.accessrules.AccessRuleId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import java.time.ZonedDateTime

data class Account<T: Organisation>(
    val id: AccountId,
    val type: AccountType,
    val accessRuleIds: List<AccessRuleId>,
    val accessExpiresOn: ZonedDateTime?,
    val organisation: T
)

enum class AccountType {
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
    open val postcode: String? = null
) {
    abstract fun type(): OrganisationType
}

data class School(
    override val name: String,
    override val country: Country,
    override val state: State? = null,
    override val postcode: String? = null,
    val district: Account<District>?,
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
    override val state: State? = null,
    val allowsOverridingUserIds: Boolean = false
) : Organisation(
    name = name,
    country = country,
    state = state
) {
    override fun type(): OrganisationType {
        return OrganisationType.API
    }
}

