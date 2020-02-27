package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.AccountNotFoundException
import com.boclips.users.domain.model.account.ApiIntegration
import com.boclips.users.domain.model.account.Organisation
import com.boclips.users.domain.service.AccountRepository
import org.springframework.stereotype.Service

@Service
class GetApiIntegrationByName(
    private val repository: AccountRepository
) {
    operator fun invoke(name: String): Organisation<ApiIntegration> {
        return repository.findApiIntegrationByName(name) ?: throw AccountNotFoundException(name)
    }
}
