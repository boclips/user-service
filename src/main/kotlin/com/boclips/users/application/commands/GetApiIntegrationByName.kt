package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.domain.model.account.ApiIntegration
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.service.AccountRepository
import org.springframework.stereotype.Service

@Service
class GetApiIntegrationByName(
    private val repository: AccountRepository
) {
    operator fun invoke(name: String): Account<ApiIntegration> {
        return repository.findApiIntegrationByName(name) ?: throw OrganisationNotFoundException(name)
    }
}
