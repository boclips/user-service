package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.infrastructure.organisation.LocationDocument
import com.boclips.users.infrastructure.organisation.OrganisationDocument
import org.bson.types.ObjectId
import java.time.ZonedDateTime

class OrganisationDocumentFactory {
    companion object {
        fun sample(
            id: ObjectId = ObjectId(),
            name: String = "The Best Organisation",
            domain: String? = null,
            role: String? = null,
            type: OrganisationType = OrganisationType.SCHOOL,
            tags: Set<String>? = null,
            externalId: String? = "external-id",
            country: LocationDocument? = LocationDocumentFactory.country(),
            state: LocationDocument? = LocationDocumentFactory.state(),
            postcode: String? = null,
            allowsOverridingUserIds: Boolean? = null,
            parent: OrganisationDocument? = null,
            billing: Boolean? = null,
            accessExpiresOn: ZonedDateTime? = null,
            features: Map<String, Boolean>? = null
        ) = OrganisationDocument(
            _id = id,
            name = name,
            domain = domain,
            role = role,
            tags = tags,
            externalId = externalId,
            type = type,
            country = country,
            state = state,
            postcode = postcode,
            allowsOverridingUserIds = allowsOverridingUserIds,
            parent = parent,
            billing = billing,
            accessExpiresOn = accessExpiresOn?.toInstant(),
            features = features
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
