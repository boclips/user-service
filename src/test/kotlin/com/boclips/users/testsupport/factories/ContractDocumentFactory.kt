package com.boclips.users.testsupport.factories

import com.boclips.users.infrastructure.contract.ContractDocument
import org.bson.types.ObjectId

class ContractDocumentFactory {
    companion object {
        fun sampleSelectedCollectionsContractDocument(
            name: String = "Test selected content contract",
            collectionIds: List<String> = emptyList()
        ) = ContractDocument.SelectedCollections().apply {
            this.id = ObjectId()
            this.name = name
            this.collectionIds = collectionIds
        }
    }
}