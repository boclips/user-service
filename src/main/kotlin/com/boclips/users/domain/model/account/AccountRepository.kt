package com.boclips.users.domain.model.account

import com.boclips.users.domain.model.UserId
import com.boclips.users.infrastructure.account.UserDocument

interface AccountRepository {
    fun activate(id: UserId): Account?
    fun save(account: Account): Account
    fun findById(id: UserId): Account?
    fun findAll(ids: List<UserId>): List<Account>
    fun findAll(): List<Account>
}