package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
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
                organisation = OrganisationResource(
                    name = organisationAccount.organisation.name,
                    type = organisationAccount.organisation.type().toString(),
                    state = organisationAccount.organisation.state?.let {
                        StateResource(
                            name = it.name,
                            id = it.id
                        )
                    },
                    country = organisationAccount.organisation.country?.let {
                        CountryResource(
                            name = it.name,
                            id = it.id,
                            states = null
                        )
                    }
                )),
            listOfNotNull(
                organisationLinkBuilder.self(organisationAccount.id),
                organisationLinkBuilder.edit(organisationAccount.id)
            )
        )
    }
}

