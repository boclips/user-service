package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.presentation.resources.AccountResource
import org.springframework.hateoas.EntityModel
import org.springframework.stereotype.Component

@Component
class AccountConverter(private val organisationLinkBuilder: OrganisationLinkBuilder) {
    fun toResource(organisation: Organisation<*>): EntityModel<AccountResource> {
        return EntityModel(
            AccountResource(
                id = organisation.id.value,
                accessRuleIds = organisation.accessRuleIds.map { it.value },
                accessExpiresOn = organisation.accessExpiresOn,
                organisation = OrganisationConverter().toResource(organisation.organisation)
            ),
            listOfNotNull(
                organisationLinkBuilder.self(organisation.id),
                organisationLinkBuilder.edit(organisation.id)
            )
        )
    }
}

