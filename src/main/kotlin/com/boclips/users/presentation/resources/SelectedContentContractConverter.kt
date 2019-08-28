package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.contract.SelectedContentContract
import org.springframework.stereotype.Service

@Service
class SelectedContentContractConverter {
    fun convert(contract: SelectedContentContract): ContractResource {
        return ContractResource(
            contract.id.value,
            contract.name,
            contract.collectionIds.map { it.value })
    }
}
