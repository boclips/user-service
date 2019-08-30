package com.boclips.users.domain.model

import com.boclips.users.domain.model.organisation.OrganisationId

sealed class Platform {
    data class ApiCustomer(val organisationId: OrganisationId) : Platform()
    data class District(val organisationId: OrganisationId) : Platform()
    object BoclipsForTeachers : Platform()

    fun getIdentifier(): OrganisationId? = when (this) {
        is BoclipsForTeachers -> null
        is ApiCustomer -> this.organisationId
        is District -> this.organisationId
    }
}