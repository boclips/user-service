package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.presentation.resources.OrganisationResource
import org.springframework.hateoas.EntityModel
import org.springframework.stereotype.Component

@Component
class OrganisationConverter(private val organisationLinkBuilder: OrganisationLinkBuilder) {
    fun toResource(organisation: Organisation<*>): EntityModel<OrganisationResource> {
        return EntityModel(
            OrganisationResource(
                id = organisation.id.value,
                contentPackageId = organisation.contentPackageId?.value,
                accessExpiresOn = organisation.accessExpiresOn,
                organisationDetails = OrganisationDetailsConverter().toResource(organisation.details)
            ),
            listOfNotNull(
                organisationLinkBuilder.self(organisation.id),
                organisationLinkBuilder.editOrganisation(organisation.id),
                organisationLinkBuilder.associateUsersToOrganisation(organisation.id)
            )
        )
    }
}

