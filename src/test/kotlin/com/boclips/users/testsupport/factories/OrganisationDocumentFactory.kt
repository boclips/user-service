package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.organisation.OrganisationAccountType
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.infrastructure.organisation.LocationDocument
import com.boclips.users.infrastructure.organisation.OrganisationDocument
import org.bson.types.ObjectId
import java.time.ZonedDateTime
import java.util.Collections.emptyList

class OrganisationDocumentFactory {
    companion object {
        fun sample(
            name: String = "The Best Organisation",
            role: String? = null,
            contractIds: List<String> = emptyList(),
            type: OrganisationType = OrganisationType.SCHOOL,
            accountType: OrganisationAccountType? = null,
            externalId: String? = "external-id",
            country: LocationDocument? = LocationDocumentFactory.country(),
            state: LocationDocument? = LocationDocumentFactory.state(),
            postcode: String? = null,
            parentOrganisation: OrganisationDocument? = null,
            accessExpiry: ZonedDateTime? = null
        ) = OrganisationDocument(
            id = ObjectId().toHexString(),
            accountType = accountType,
            name = name,
            role = role,
            contractIds = contractIds,
            externalId = externalId,
            type = type,
            country = country,
            state = state,
            postcode = postcode,
            parentOrganisation = parentOrganisation,
            accessExpiry = accessExpiry?.toInstant()
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
