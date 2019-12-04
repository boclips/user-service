package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.presentation.resources.school.CountryResource
import com.boclips.users.presentation.resources.school.StateResource
import org.springframework.hateoas.Resource
import org.springframework.stereotype.Component

@Component
class OrganisationConverter(private val organisationLinkBuilder: OrganisationLinkBuilder) {
    fun toResource(organisationAccount: OrganisationAccount<*>): Resource<OrganisationAccountResource> {
        return Resource(
            OrganisationAccountResource(
                name = organisationAccount.organisation.name,
                contractIds = organisationAccount.contractIds.map { it.value },
                accessExpiresOn = organisationAccount.accessExpiresOn,
                type = organisationAccount.organisation.type().toString(),
                organisation = OrganisationResource(
                    name = organisationAccount.organisation.name,
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
                organisationLinkBuilder.self(organisationAccount.id)
            )
        )
    }
}

