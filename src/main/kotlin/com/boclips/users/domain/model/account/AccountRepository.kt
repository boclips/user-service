package com.boclips.users.domain.model.account

interface AccountRepository {
    fun save(account: Account): Account
    fun findById(id: String): Account?
    fun findAll(ids: List<String>): List<Account>
}