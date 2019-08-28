package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.ContractId
import org.springframework.stereotype.Service

@Service
class ContractDocumentConverter {
    fun fromDocument(document: ContractDocument): Contract {
        return when (document) {
            is ContractDocument.SelectedContent -> Contract.SelectedContent(
                id = ContractId(document.id.toHexString()),
                name = document.name,
                collectionIds = document.collectionIds.map { CollectionId(it) }
            )
        }
    }
}
