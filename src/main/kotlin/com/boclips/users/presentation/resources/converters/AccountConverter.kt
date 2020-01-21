package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.account.Account
import com.boclips.users.presentation.hateoas.AccountLinkBuilder
import com.boclips.users.presentation.resources.AccountResource
import org.springframework.hateoas.EntityModel
import org.springframework.stereotype.Component

@Component
class AccountConverter(private val accountLinkBuilder: AccountLinkBuilder) {
    fun toResource(account: Account<*>): EntityModel<AccountResource> {
        return EntityModel(
            AccountResource(
                id = account.id.value,
                contractIds = account.contractIds.map { it.value },
                accessExpiresOn = account.accessExpiresOn,
                organisation = OrganisationConverter().toResource(account.organisation)
            ),
            listOfNotNull(
                accountLinkBuilder.self(account.id),
                accountLinkBuilder.edit(account.id)
            )
        )
    }
}

