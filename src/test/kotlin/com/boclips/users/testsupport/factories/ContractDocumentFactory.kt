package com.boclips.users.testsupport.factories

import com.boclips.users.infrastructure.contract.ContractDocument
import org.bson.types.ObjectId

class ContractDocumentFactory {
    companion object {
        fun sampleSelectedContentContractDocument(
            name: String = "Test selected content contract",
            collectionIds: List<String> = emptyList()
        ) = ContractDocument.SelectedContent(id = ObjectId(), name = name, collectionIds = collectionIds)
    }
}