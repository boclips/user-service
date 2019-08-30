package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.infrastructure.organisation.OrganisationTypeDocument
import com.boclips.users.infrastructure.organisation.OrganisationTypeDocument.Companion.TYPE_API
import com.boclips.users.infrastructure.organisation.OrganisationTypeDocument.Companion.TYPE_DISTRICT
import com.boclips.users.infrastructure.organisation.OrganisationTypeDocument.Companion.TYPE_NO_ORGANISATION

class OrganisationTypeConverter {
    fun toDocument(organisationType: OrganisationType): OrganisationTypeDocument {
        return OrganisationTypeDocument(
            id = extractOrganisationId(organisationType),
            type = when (organisationType) {
                is OrganisationType.ApiCustomer -> TYPE_API
                is OrganisationType.District -> TYPE_DISTRICT
                OrganisationType.BoclipsForTeachers -> TYPE_NO_ORGANISATION
            }
        )
    }

    private fun extractOrganisationId(organisationType: OrganisationType): String? {
        return when (organisationType) {
            is OrganisationType.BoclipsForTeachers -> null
            is OrganisationType.ApiCustomer -> organisationType.organisationId.value
            is OrganisationType.District -> organisationType.organisationId.value
        }
    }
}
