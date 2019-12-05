package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.presentation.resources.OrganisationAccountResource
import com.boclips.users.presentation.resources.OrganisationResource
import com.boclips.users.presentation.resources.school.CountryResource
import com.boclips.users.presentation.resources.school.StateResource
import org.springframework.hateoas.Resource
import org.springframework.stereotype.Component

@Component
class OrganisationAccountConverter(private val organisationLinkBuilder: OrganisationLinkBuilder) {
    fun toResource(organisationAccount: OrganisationAccount<*>): Resource<OrganisationAccountResource> {
        return Resource(
            OrganisationAccountResource(
                id = organisationAccount.id.value,
                contractIds = organisationAccount.contractIds.map { it.value },
                accessExpiresOn = organisationAccount.accessExpiresOn,
                organisation = OrganisationConverter().toResource(organisationAccount.organisation)),
            listOfNotNull(
                organisationLinkBuilder.self(organisationAccount.id),
                organisationLinkBuilder.edit(organisationAccount.id)
            )
        )
    }
}

