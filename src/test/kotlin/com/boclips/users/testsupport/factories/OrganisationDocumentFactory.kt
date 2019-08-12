package com.boclips.users.testsupport.factories

import com.boclips.users.infrastructure.organisation.OrganisationDocument
import org.bson.types.ObjectId

class OrganisationDocumentFactory {
    companion object {
        fun sample(
            name: String = "The Best Organisation"
        ) = OrganisationDocument(ObjectId(), name)
    }
}