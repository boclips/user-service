package com.boclips.users.domain.model.account

data class Account(
    val id: AccountId,
    val name: String,
    val products: Set<AccountProducts>?
)
