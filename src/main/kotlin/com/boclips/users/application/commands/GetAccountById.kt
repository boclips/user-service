package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.OrganisationAccountId
import com.boclips.users.domain.service.AccountRepository
import org.springframework.stereotype.Service

@Service
class GetAccountById(
    private val repository: AccountRepository
) {
    operator fun invoke(id: String): Account<*> {
        return repository.findOrganisationAccountById(OrganisationAccountId(id)) ?: throw OrganisationNotFoundException(id)
    }
}
