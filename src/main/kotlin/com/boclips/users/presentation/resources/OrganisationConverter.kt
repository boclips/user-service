package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.organisation.OrganisationAccount
import org.springframework.stereotype.Component

@Component
class OrganisationConverter {
    fun toResource(organisation: OrganisationAccount<*>) = OrganisationAccountResource(
        organisation.organisation.name,
        organisation.contractIds.map { it.value }
    )
}
