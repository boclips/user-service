package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.account.Account
import com.boclips.users.presentation.hateoas.AccountLinkBuilder
import com.boclips.users.presentation.resources.AccountResource
import org.springframework.hateoas.Resource
import org.springframework.stereotype.Component

@Component
class AccountConverter(private val accountLinkBuilder: AccountLinkBuilder) {
    fun toResource(account: Account<*>): Resource<AccountResource> {
        return Resource(
            AccountResource(
                id = account.id.value,
                contractIds = account.contractIds.map { it.value },
                accessExpiresOn = account.accessExpiresOn,
                organisation = OrganisationConverter().toResource(account.organisation)),
            listOfNotNull(
                accountLinkBuilder.edit(account.id)
            )
        )
    }
}

