package com.boclips.users.presentation.resources.converters

import com.boclips.users.api.response.organisation.OrganisationDetailsResource
import com.boclips.users.api.response.state.StateResource
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.OrganisationDetails
import com.boclips.users.presentation.resources.school.CountryConverter
import org.springframework.stereotype.Component

@Component
class OrganisationDetailsConverter(private val countryConverter: CountryConverter) {
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
                countryConverter.toCountryResource(it)
            },
            allowsOverridingUserIds = when (organisationDetails) {
                is ApiIntegration -> organisationDetails.allowsOverridingUserIds
                else -> null
            }
        )
    }
}

