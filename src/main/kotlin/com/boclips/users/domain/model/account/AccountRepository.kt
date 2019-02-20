package com.boclips.users.domain.model.account

interface AccountRepository {
    fun save(account: Account): Account
    fun findById(id: AccountId): Account?
    fun findAll(ids: List<AccountId>): List<Account>
}