package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId

object OrganisationDocumentConverter {
    fun fromDocument(organisationDocument: OrganisationDocument) =
        Organisation(
            id = OrganisationId(organisationDocument.id.toHexString()),
            name = organisationDocument.name
        )
}