package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.presentation.hateoas.ContractLinkBuilder
import com.boclips.users.presentation.hateoas.SelectedContractLinkBuilder
import org.springframework.stereotype.Service

@Service
class ContractConverter(
    private val contractLinkBuilder: ContractLinkBuilder,
    private val selectedContractLinkBuilder: SelectedContractLinkBuilder
) {
    fun toResource(contract: Contract): ContractResource {
        return when (contract) {
            is Contract.SelectedContent -> ContractResource.SelectedContent(
                contract.name,
                contract.collectionIds.map { it.value }
            ).apply {
                add(
                    listOfNotNull(
                        contractLinkBuilder.self(contract.id),
                        selectedContractLinkBuilder.addCollection(contractId = contract.id.value),
                        selectedContractLinkBuilder.removeCollection(contractId = contract.id.value)
                    )
                )
            }
        }
    }
}
