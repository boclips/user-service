package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.contract.VideoId
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class ContractDocumentConverter {
    fun fromDocument(document: ContractDocument): Contract {
        return when (document) {
            is ContractDocument.SelectedCollections ->  Contract.SelectedCollections(
                id = ContractId(document.id.toHexString()),
                name = document.name,
                collectionIds = document.collectionIds.map { CollectionId(it) }
            )
            is ContractDocument.SelectedVideos -> Contract.SelectedVideos(
                id = ContractId(document.id.toHexString()),
                name = document.name,
                videoIds = document.videoIds.map { VideoId(it) }
            )
        }
    }

    fun toDocument(contract: Contract): ContractDocument {
        return when (contract) {
            is Contract.SelectedCollections -> ContractDocument.SelectedCollections().apply {
                id = ObjectId(contract.id.value)
                name = contract.name
                collectionIds = contract.collectionIds.map { it.value }
            }
            is Contract.SelectedVideos -> ContractDocument.SelectedVideos().apply {
                id = ObjectId(contract.id.value)
                name = contract.name
                videoIds = contract.videoIds.map { it.value }
            }
        }
    }
}
