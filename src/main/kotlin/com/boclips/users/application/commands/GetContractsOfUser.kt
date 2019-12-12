package com.boclips.users.application.commands

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.service.ContractRepository
import com.boclips.users.domain.service.AccountRepository
import org.springframework.stereotype.Service

@Service
class GetContractsOfUser(
    private val accountRepository: AccountRepository,
    private val contractRepository: ContractRepository,
    private val getOrImportUser: GetOrImportUser
) {
    operator fun invoke(userId: UserId): List<Contract> {
        return findOrganisation(userId)
            ?.contractIds?.mapNotNull(contractRepository::findById)
            ?: emptyList()
    }

    private fun findOrganisation(userId: UserId): Account<*>? {
        return getOrImportUser(userId).organisationAccountId?.let {
            accountRepository.findAccountById((it))
        }
    }
}
