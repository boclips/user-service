package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.organisation.Organisation
import org.springframework.stereotype.Service

@Service
class OrganisationConverter {
    fun toResource(organisation: Organisation)
        = OrganisationResource(
            organisation.name,
            organisation.contractIds.map { it.value }
        )
}
