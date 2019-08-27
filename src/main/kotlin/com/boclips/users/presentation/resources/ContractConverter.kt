package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.contract.ContractId
import org.springframework.stereotype.Component

@Component
class ContractConverter {
    fun convert(contractId: ContractId): ContractResource {
        return ContractResource(contractId.value)
    }
}