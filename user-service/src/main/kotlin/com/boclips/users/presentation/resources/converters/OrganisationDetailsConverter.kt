package com.boclips.users.presentation.resources.converters

import com.boclips.users.api.response.country.CountryResource
import com.boclips.users.api.response.organisation.OrganisationDetailsResource
import com.boclips.users.api.response.state.StateResource
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.OrganisationDetails
import org.springframework.stereotype.Component

@Component
class OrganisationDetailsConverter {
    fun toResource(organisationDetails: OrganisationDetails): OrganisationDetailsResource {
        return OrganisationDetailsResource(
            name = organisationDetails.name,
            domain = organisationDetails.domain,
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
                    states = null,
                    _links = null
                )
            },
            allowsOverridingUserIds = when (organisationDetails) {
                is ApiIntegration -> organisationDetails.allowsOverridingUserIds
                else -> null
            }
        )
    }
}

