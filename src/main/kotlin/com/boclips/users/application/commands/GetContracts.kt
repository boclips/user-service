package com.boclips.users.application.commands

import com.boclips.users.application.model.ContractFilter
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.service.ContractRepository
import org.springframework.stereotype.Service

@Service
class GetContracts(
    private val contractRepository: ContractRepository
) {
    operator fun invoke(filter: ContractFilter): List<Contract> {
        return if (filter.name == null) {
            contractRepository.findAll()
        } else {
            contractRepository.findAllByName(filter.name)
        }
    }
}