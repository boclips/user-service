package com.boclips.users.domain.model.account

import com.boclips.users.domain.model.UserId

interface AccountRepository {
    fun activate(id: UserId): Account?
    fun save(account: Account): Account
    fun findById(id: UserId): Account?
    fun findAll(ids: List<UserId>): List<Account>
}