package com.boclips.users.application.commands

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.service.AccountRepository
import com.boclips.users.infrastructure.organisation.AccountSearchRequest
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class GetAccounts( private val accountRepository: AccountRepository) {
    operator fun invoke (searchRequest: AccountSearchRequest): Page<Account<*>> {
        return accountRepository.findAccounts(searchRequest = searchRequest)
    }
}
