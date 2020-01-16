package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.account.ApiIntegration
import com.boclips.users.domain.model.account.Organisation
import com.boclips.users.presentation.resources.OrganisationResource
import com.boclips.users.presentation.resources.school.CountryResource
import com.boclips.users.presentation.resources.school.StateResource
import org.springframework.stereotype.Component

@Component
class OrganisationConverter {
    fun toResource(organisation: Organisation): OrganisationResource {
        return OrganisationResource(
            name = organisation.name,
            type = organisation.type().toString(),
            state = organisation.state?.let {
                StateResource(
                    name = it.name,
                    id = it.id
                )
            },
            country = organisation.country?.let {
                CountryResource(
                    name = it.name,
                    id = it.id,
                    states = null
                )
            },
            allowsOverridingUserIds = when (organisation) {
                is ApiIntegration -> organisation.allowsOverridingUserIds
                else -> null
            }
        )
    }
}

