package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.infrastructure.organisation.OrganisationTypeDocument

class OrganisationTypeConverter {
    fun toDocument(organisationType: OrganisationType): OrganisationTypeDocument {
        return OrganisationTypeDocument(
            id = extractOrganisationId(organisationType),
            type = organisationType
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
