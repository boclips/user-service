package com.boclips.users.presentation.converters

import com.boclips.users.api.response.country.CountryResource
import com.boclips.users.api.response.organisation.OrganisationDetailsResource
import com.boclips.users.api.response.state.StateResource
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.Organisation
import org.springframework.stereotype.Component

@Component
class OrganisationDetailsConverter {
    fun toResource(organisation: Organisation): OrganisationDetailsResource {
        return OrganisationDetailsResource(
            id = organisation.id.value,
            name = organisation.name,
            domain = organisation.domain,
            type = organisation.type().toString(),
            state = organisation.address.state?.let {
                StateResource(
                    name = it.name,
                    id = it.id
                )
            },
            country = organisation.address.country?.let {
                CountryResource(
                    name = it.name,
                    id = it.id,
                    states = null,
                    _links = null
                )
            },
            allowsOverridingUserIds = when (organisation) {
                is ApiIntegration -> organisation.allowsOverridingUserIds
                else -> null
            },
            features = organisation.features?.let { FeatureConverter.toFeatureResource(features = it) },
            logoUrl = organisation.logoUrl
        )
    }
}
