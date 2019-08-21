package com.boclips.users.domain.model

import com.boclips.users.domain.model.organisation.OrganisationId

sealed class Platform {
    object BoclipsForTeachers : Platform()
    data class ApiCustomer(val organisationId: OrganisationId) : Platform()

    fun getIdentifier(): OrganisationId? = when (this) {
        is BoclipsForTeachers -> null
        is ApiCustomer -> this.organisationId
    }
}
