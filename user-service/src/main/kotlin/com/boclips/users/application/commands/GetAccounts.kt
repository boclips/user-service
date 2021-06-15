package com.boclips.users.application.commands

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountRepository
import org.springframework.stereotype.Component

@Component
class GetAccounts(private val accountsRepository: AccountRepository) {
    operator fun invoke(): List<Account> {
        return accountsRepository.findAll()
    }
}
