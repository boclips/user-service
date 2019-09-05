package com.boclips.users.application.commands

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.service.SelectedContentContractRepository
import com.boclips.users.presentation.requests.CreateContractRequest
import org.springframework.stereotype.Service

@Service
class CreateContract(private val selectedContentContractRepository: SelectedContentContractRepository) {
    operator fun invoke(request: CreateContractRequest): Contract {
        return when (request) {
            is CreateContractRequest.SelectedContent -> selectedContentContractRepository.saveSelectedContentContract(
                request.name!!,
                request.collectionIds!!.map { CollectionId(it) }
            )
        }
    }
}