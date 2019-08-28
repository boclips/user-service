package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.contract.Contract
import org.springframework.stereotype.Service

@Service
class ContractConverter {
    fun toResource(contract: Contract): ContractResource {
        return when (contract) {
            is Contract.SelectedContent -> ContractResource.SelectedContent(
                contract.id.value,
                contract.name,
                contract.collectionIds.map { it.value }
            )
        }
    }
}
