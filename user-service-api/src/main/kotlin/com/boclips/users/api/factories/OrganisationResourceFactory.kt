package com.boclips.users.api.factories

import com.boclips.users.api.response.country.CountryResource
import com.boclips.users.api.response.organisation.OrganisationDetailsResource
import com.boclips.users.api.response.organisation.OrganisationResource
import com.boclips.users.api.response.state.StateResource
import org.springframework.hateoas.Link
import java.time.ZonedDateTime
import java.util.UUID

class OrganisationResourceFactory {
    companion object {
        @JvmStatic
        fun sample(
            id: String = UUID.randomUUID().toString(),
            accessExpiresOn: ZonedDateTime? = null,
            contentPackageId: String? = null,
            organisationDetails: OrganisationDetailsResource = sampleDetails(),
            _links: Map<String, Link>? = null
        ) = OrganisationResource(
            id = id,
            accessExpiresOn = accessExpiresOn,
            contentPackageId = contentPackageId,
            organisationDetails = organisationDetails,
            _links = _links
        )

        @JvmStatic
        fun sampleDetails(
            id: String = UniqueId(),
            name: String = "Sample Organisation Details",
            domain: String? = null,
            type: String? = "API",
            state: StateResource? = null,
            country: CountryResource? = null,
            allowsOverridingUserIds: Boolean? = null
        ) = OrganisationDetailsResource(
            id = id,
            name = name,
            domain = domain,
            type = type,
            state = state,
            country = country,
            allowsOverridingUserIds = allowsOverridingUserIds
        )
    }
}
