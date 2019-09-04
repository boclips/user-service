package com.boclips.users.testsupport.factories

import com.boclips.users.infrastructure.organisation.OrganisationDocument
import org.bson.types.ObjectId
import java.util.Collections.emptyList

class OrganisationDocumentFactory {
    companion object {
        fun sample(
            name: String = "The Best Organisation",
            role: String? = null,
            contractIds: List<String> = emptyList(),
            externalId: String? = "external-id",
            countryId: String? = "USA",
            stateId: String? = "NY"
        ) = OrganisationDocument(
            id = ObjectId(),
            name = name,
            role = role,
            contractIds = contractIds,
            externalId = externalId,
            type = null,
            country = countryId,
            state = stateId
        )
    }
}
