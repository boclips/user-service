package com.boclips.users.domain.service

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.UserId

interface AccountProvider {
    fun getAccountById(id: UserId): Account?
    fun getAccounts(): Sequence<Account>
    fun createAccount(email: String, password: String): Account
    fun count(): Int
}
