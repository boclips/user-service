package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.AccountAlreadyExistsException
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.account.ApiIntegration
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.service.AccountRepository
import com.boclips.users.presentation.requests.CreateAccountRequest
import org.springframework.stereotype.Service

@Service
class CreateApiIntegration(
    private val repository: AccountRepository
) {
    operator fun invoke(request: CreateAccountRequest): Account<ApiIntegration> {
        assertNewApiIntegrationDoesNotCollide(request)

        return repository.save(
            apiIntegration = ApiIntegration(
                name = request.name!!
            ),
            contractIds = request.contractIds!!.map { ContractId(it) },
            role = request.role
        )
    }

    private fun assertNewApiIntegrationDoesNotCollide(request: CreateAccountRequest) {
        repository.findApiIntegrationByName(request.name!!)?.let {
            throw AccountAlreadyExistsException(request.name!!)
        }
        repository.findApiIntegrationByRole(request.role!!)?.let {
            throw AccountAlreadyExistsException(request.role!!)
        }
    }
}
