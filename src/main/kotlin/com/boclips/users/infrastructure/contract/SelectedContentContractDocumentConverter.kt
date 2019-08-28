package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.contract.SelectedContentContract
import org.springframework.stereotype.Service

@Service
class SelectedContentContractDocumentConverter {
    fun fromDocument(document: SelectedContentContractDocument): SelectedContentContract {
        return SelectedContentContract(
            id = ContractId(document.id.toHexString()),
            name = document.name,
            collectionIds = document.collectionIds.map { CollectionId(it) }
        )
    }
}
