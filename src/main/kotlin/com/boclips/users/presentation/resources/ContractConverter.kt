package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.presentation.hateoas.ContractLinkBuilder
import org.springframework.stereotype.Service

@Service
class ContractConverter(
    private val contractLinkBuilder: ContractLinkBuilder
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
                    contractLinkBuilder.self(contract.id)
                )
            )
        }
    }
}
