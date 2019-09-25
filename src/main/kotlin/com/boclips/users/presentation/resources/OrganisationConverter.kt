package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.organisation.OrganisationAccount
import org.springframework.stereotype.Component

@Component
class OrganisationConverter {
    fun toResource(organisationAccount: OrganisationAccount<*>) = OrganisationAccountResource(
        organisationAccount.organisation.name,
        organisationAccount.contractIds.map { it.value }
    )
}
