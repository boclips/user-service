package com.boclips.users.testsupport.factories

import com.boclips.users.infrastructure.organisation.OrganisationDocument
import org.bson.types.ObjectId

class OrganisationDocumentFactory {
    companion object {
        fun sample(
            name: String = "The Best Organisation",
            role: String? = null,
            contractIds: List<String> = emptyList()
        ) = OrganisationDocument(id = ObjectId(), name = name, role = role, contractIds = contractIds)
    }
}
