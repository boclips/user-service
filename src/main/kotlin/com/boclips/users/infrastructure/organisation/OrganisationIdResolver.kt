package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.OrganisationAccountId

interface OrganisationIdResolver {
    fun resolve(roles: List<String>): OrganisationAccountId?
}
