package com.boclips.users.presentation.converters

import com.boclips.users.api.response.country.CountryResource
import com.boclips.users.api.response.organisation.OrganisationDetailsResource
import com.boclips.users.api.response.state.StateResource
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.Organisation
import org.springframework.stereotype.Component

@Component
class OrganisationDetailsConverter {
    fun toResource(organisation: Organisation<*>): OrganisationDetailsResource {
        return OrganisationDetailsResource(
            id = organisation.id.value,
            name = organisation.details.name,
            domain = organisation.details.domain,
            type = organisation.details.type().toString(),
            state = organisation.details.state?.let {
                StateResource(
                    name = it.name,
                    id = it.id
                )
            },
            country = organisation.details.country?.let {
                CountryResource(
                    name = it.name,
                    id = it.id,
                    states = null,
                    _links = null
                )
            },
            allowsOverridingUserIds = when (organisation.details) {
                is ApiIntegration -> organisation.details.allowsOverridingUserIds
                else -> null
            }
        )
    }
}

