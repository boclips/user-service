package com.boclips.users.domain.model.account

import com.boclips.users.infrastructure.account.AccountDocument

interface AccountRepository {
    fun create(account: AccountDocument): Account
    fun findAll(): List<Account>
}
