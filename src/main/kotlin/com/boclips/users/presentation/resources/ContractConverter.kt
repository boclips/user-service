package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.presentation.hateoas.ContractsLinkBuilder
import org.springframework.stereotype.Service

@Service
class ContractConverter(
    private val contractsLinkBuilder: ContractsLinkBuilder
) {
    fun toResource(contract: Contract): ContractResource {
        return when (contract) {
            is Contract.SelectedContent -> ContractResource.SelectedContent(
                contract.name,
                contract.collectionIds.map { it.value }
            )
        }.apply {
            add(
                listOfNotNull(
                    contractsLinkBuilder.self(contract.id)
                )
            )
        }
    }
}
