package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.AccountNotFoundException
import com.boclips.users.domain.model.account.Organisation
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.service.AccountRepository
import org.springframework.stereotype.Service

@Service
class GetAccountById(
    private val repository: AccountRepository
) {
    operator fun invoke(id: String): Organisation<*> {
        return repository.findAccountById(AccountId(id)) ?: throw AccountNotFoundException(id)
    }
}
