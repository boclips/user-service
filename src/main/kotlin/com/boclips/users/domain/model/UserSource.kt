package com.boclips.users.domain.model

import com.boclips.users.domain.model.organisation.OrganisationId

sealed class UserSource {
    object Boclips : UserSource()
    data class ApiClient(val organisationId: OrganisationId) : UserSource()
}
