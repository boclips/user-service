package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.OrganisationId

interface OrganisationIdResolver {
    fun resolve(roles: List<String>): OrganisationId?
}
