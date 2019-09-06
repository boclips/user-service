package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.organisation.OrganisationAccount
import org.springframework.stereotype.Service

@Service
class OrganisationConverter {
    fun toResource(organisation: OrganisationAccount) = OrganisationResource(
        organisation.organisation.name,
        organisation.contractIds.map { it.value }
    )
}
