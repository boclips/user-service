package com.boclips.users.application.commands

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.service.ContractRepository
import com.boclips.users.domain.service.OrganisationAccountRepository
import org.springframework.stereotype.Service

@Service
class GetContractsOfUser(
    private val organisationAccountRepository: OrganisationAccountRepository,
    private val contractRepository: ContractRepository,
    private val getUser: GetUser
) {
    operator fun invoke(userId: UserId): List<Contract> {
        return findOrganisation(userId)
            ?.contractIds?.mapNotNull(contractRepository::findById)
            ?: emptyList()
    }

    private fun findOrganisation(userId: UserId): OrganisationAccount? {
        return getUser(userId.value).organisationAccountId?.let {
            organisationAccountRepository.findOrganisationAccountById(it)
        }
    }
}