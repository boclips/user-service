package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.account.OrganisationId

interface OrganisationIdResolver {
    fun resolve(roles: List<String>): OrganisationId?
}
