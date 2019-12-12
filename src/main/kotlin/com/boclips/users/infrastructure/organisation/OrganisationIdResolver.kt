package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.account.AccountId

interface OrganisationIdResolver {
    fun resolve(roles: List<String>): AccountId?
}
