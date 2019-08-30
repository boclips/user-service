package com.boclips.users.domain.model

import com.boclips.users.domain.model.organisation.OrganisationId

sealed class OrganisationType {
    data class ApiCustomer(val organisationId: OrganisationId) : OrganisationType()
    data class District(val organisationId: OrganisationId) : OrganisationType()
    object BoclipsForTeachers : OrganisationType()

    fun getIdentifier(): OrganisationId? = when (this) {
        is BoclipsForTeachers -> null
        is ApiCustomer -> this.organisationId
        is District -> this.organisationId
    }
}