package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.ContractNotFoundException
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.service.ContractRepository
import org.springframework.stereotype.Service

@Service
class GetContractByName(
    private val contractRepository: ContractRepository
) {
    operator fun invoke(name: String): Contract {
        return contractRepository.findByName(name) ?: throw ContractNotFoundException(name)
    }
}
