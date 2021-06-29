package com.boclips.users.presentation.converters

import com.boclips.users.domain.model.account.Account
import com.boclips.users.presentation.resources.AccountResource
import com.boclips.users.presentation.resources.AccountsResource
import com.boclips.users.presentation.resources.AccountsWrapper
import org.springframework.stereotype.Component

@Component
class AccountConverter {
    fun toAccountsResource(accounts: List<Account>): AccountsResource {
        val accountResources =
            accounts.map { AccountResource(id = it.id.value, name = it.name, products = it.products) }
        return AccountsResource(AccountsWrapper(accountResources))
    }
}
