package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.account.AccountProduct

open class AccountsResource(
    val _embedded: AccountsWrapper
)

data class AccountsWrapper(val accounts: List<AccountResource>)

data class AccountResource(
    val id: String,
    val name: String,
    val products: Set<AccountProduct>?
)
