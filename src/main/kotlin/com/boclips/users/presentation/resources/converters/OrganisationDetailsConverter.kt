package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.OrganisationDetails
import com.boclips.users.presentation.resources.OrganisationDetailsResource
import com.boclips.users.presentation.resources.school.CountryResource
import com.boclips.users.presentation.resources.school.StateResource
import org.springframework.stereotype.Component

@Component
class OrganisationDetailsConverter {
    fun toResource(organisationDetails: OrganisationDetails): OrganisationDetailsResource {
        return OrganisationDetailsResource(
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

