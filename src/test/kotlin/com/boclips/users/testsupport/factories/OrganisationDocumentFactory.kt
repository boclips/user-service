package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.account.DealType
import com.boclips.users.domain.model.account.OrganisationType
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
            accessRuleIds: List<String> = emptyList(),
            type: OrganisationType = OrganisationType.SCHOOL,
            dealType: DealType? = null,
            externalId: String? = "external-id",
            country: LocationDocument? = LocationDocumentFactory.country(),
            state: LocationDocument? = LocationDocumentFactory.state(),
            postcode: String? = null,
            allowsOverridingUserIds: Boolean? = null,
            parentOrganisation: OrganisationDocument? = null,
            accessExpiresOn: ZonedDateTime? = null
        ) = OrganisationDocument(
            id = ObjectId().toHexString(),
            dealType = dealType,
            name = name,
            role = role,
            accessRuleIds = accessRuleIds,
            externalId = externalId,
            type = type,
            country = country,
            state = state,
            postcode = postcode,
            allowsOverridingUserIds = allowsOverridingUserIds,
            parentOrganisation = parentOrganisation,
            accessExpiresOn = accessExpiresOn?.toInstant()
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
