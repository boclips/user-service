package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.ContractNotFoundException
import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.service.ContractRepository
import org.springframework.stereotype.Service

@Service
class AddCollectionToContract(
    private val contractRepository: ContractRepository
) {
    operator fun invoke(contractId: ContractId, collectionId: CollectionId) {
        contractRepository
            .findById(contractId)
            ?.let {
                when (it) {
                    is Contract.SelectedCollections -> {
                        val updatedContract = it.copy(
                            collectionIds = it.collectionIds.toMutableSet().apply { add(collectionId) }.toList()
                        )
                        contractRepository.save(updatedContract)
                    }
                }
            } ?: throw ContractNotFoundException(contractId.value)
    }
}