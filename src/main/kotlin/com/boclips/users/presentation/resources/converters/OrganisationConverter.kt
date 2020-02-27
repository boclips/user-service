package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.account.ApiIntegration
import com.boclips.users.domain.model.account.OrganisationDetails
import com.boclips.users.presentation.resources.OrganisationResource
import com.boclips.users.presentation.resources.school.CountryResource
import com.boclips.users.presentation.resources.school.StateResource
import org.springframework.stereotype.Component

@Component
class OrganisationConverter {
    fun toResource(organisationDetails: OrganisationDetails): OrganisationResource {
        return OrganisationResource(
            name = organisationDetails.name,
            type = organisationDetails.type().toString(),
            state = organisationDetails.state?.let {
                StateResource(
                    name = it.name,
                    id = it.id
                )
            },
            country = organisationDetails.country?.let {
                CountryResource(
                    name = it.name,
                    id = it.id,
                    states = null
                )
            },
            allowsOverridingUserIds = when (organisationDetails) {
                is ApiIntegration -> organisationDetails.allowsOverridingUserIds
                else -> null
            }
        )
    }
}

