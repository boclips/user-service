package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State

sealed class OrganisationDetails(
    open val name: String,
    open val country: Country? = null,
    open val state: State? = null,
    open val postcode: String? = null,
    open val domain: String? = null
) {
    abstract fun type(): OrganisationType
}

data class School(
    override val name: String,
    override val country: Country,
    override val domain: String? = null,
    override val state: State? = null,
    override val postcode: String? = null,
    val district: Organisation<District>?,
    val externalId: String?
) : OrganisationDetails(
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
    override val domain: String? = null,
    val externalId: String
) : OrganisationDetails(
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
    override val domain: String? = null,
    override val country: Country? = null,
    override val state: State? = null,
    val allowsOverridingUserIds: Boolean = false
) : OrganisationDetails(
    name = name,
    country = country,
    state = state
) {
    override fun type(): OrganisationType {
        return OrganisationType.API
    }
}