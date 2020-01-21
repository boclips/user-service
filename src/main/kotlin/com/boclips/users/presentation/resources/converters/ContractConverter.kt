package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.presentation.hateoas.ContractLinkBuilder
import com.boclips.users.presentation.hateoas.SelectedContractLinkBuilder
import com.boclips.users.presentation.resources.ContractResource
import org.springframework.stereotype.Service

@Service
class ContractConverter(
    private val contractLinkBuilder: ContractLinkBuilder,
    private val selectedContractLinkBuilder: SelectedContractLinkBuilder
) {
    fun toResource(contract: Contract): ContractResource {
        return when (contract) {
            is Contract.SelectedCollections -> ContractResource.SelectedCollections(
                name = contract.name,
                collectionIds = contract.collectionIds.map { it.value },
                _links = listOfNotNull(
                    selectedContractLinkBuilder.addCollection(contractId = contract.id.value),
                    selectedContractLinkBuilder.removeCollection(contractId = contract.id.value),
                    contractLinkBuilder.self(contract.id)
                ).map { it.rel.value() to it }.toMap()
            )
            is Contract.SelectedVideos -> ContractResource.SelectedVideos(
                name = contract.name,
                videoIds = contract.videoIds.map { it.value },
                _links = listOfNotNull(contractLinkBuilder.self(contract.id)).map { it.rel.value() to it }.toMap()
            )
        }
    }
}
