package com.boclips.users.presentation.converters

import com.boclips.users.api.response.organisation.OrganisationResource
import com.boclips.users.api.response.organisation.OrganisationsResource
import com.boclips.users.api.response.organisation.OrganisationsWrapper
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.domain.model.Page
import org.springframework.hateoas.PagedModel
import org.springframework.stereotype.Component

@Component
class OrganisationConverter(
    private val organisationLinkBuilder: OrganisationLinkBuilder
) {
    fun toResource(organisation: Organisation<*>): OrganisationResource {
        return OrganisationResource(
            id = organisation.id.value,
            contentPackageId = organisation.contentPackageId?.value,
            accessExpiresOn = organisation.accessExpiresOn,
            organisationDetails = OrganisationDetailsConverter().toResource(organisation),
            _links = listOfNotNull(
                organisationLinkBuilder.self(organisation.id),
                organisationLinkBuilder.editOrganisation(organisation.id),
                organisationLinkBuilder.associateUsersToOrganisation(organisation.id)
            ).map { it.rel.value() to it }.toMap()
        )
    }

    fun toResource(organisations: Page<Organisation<*>>): OrganisationsResource {
        return OrganisationsResource(
            _embedded = OrganisationsWrapper(
                organisations = organisations.items.map {
                    toResource(it)
                }),
            page = PagedModel.PageMetadata(
                organisations.pageSize.toLong(),
                organisations.pageNumber.toLong(),
                organisations.totalElements
            ),
            _links = null
        )
    }
}

