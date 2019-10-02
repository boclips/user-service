package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.infrastructure.organisation.LocationDocument
import com.boclips.users.infrastructure.organisation.OrganisationDocument
import org.bson.types.ObjectId
import java.util.Collections.emptyList

class OrganisationDocumentFactory {
    companion object {
        fun sample(
            name: String = "The Best Organisation",
            role: String? = null,
            contractIds: List<String> = emptyList(),
            type: OrganisationType = OrganisationType.SCHOOL,
            externalId: String? = "external-id",
            country: LocationDocument? = LocationDocumentFactory.country(),
            state: LocationDocument? = LocationDocumentFactory.state(),
            parentOrganisation: OrganisationDocument? = null
        ) = OrganisationDocument(
            id = ObjectId().toHexString(),
            name = name,
            role = role,
            contractIds = contractIds,
            externalId = externalId,
            type = type,
            country = country,
            state = state,
            parentOrganisation = parentOrganisation
        )
    }
}

class LocationDocumentFactory {
    companion object {
        fun country(
            code: String = "USA"
        ) = LocationDocument(
            code = code
        )

        fun state(
            code: String = "IL"
        ) = LocationDocument(
            code = code
        )
    }
}
