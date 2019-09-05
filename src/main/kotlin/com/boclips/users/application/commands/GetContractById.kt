package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.ContractNotFoundException
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.service.ContractRepository
import org.springframework.stereotype.Service

@Service
class GetContractById(private val contractRepository: ContractRepository) {
    operator fun invoke(id: String): Contract {
        val contractId = ContractId(id)
        return contractRepository.findById(contractId) ?: throw ContractNotFoundException(id)
    }
}