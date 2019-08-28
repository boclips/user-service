package com.boclips.users.testsupport.factories

import com.boclips.users.infrastructure.contract.SelectedContentContractDocument
import org.bson.types.ObjectId

class SelectedContentContractDocumentFactory {
    companion object {
        fun sample(
            name: String = "Test selected content contract",
            collectionIds: List<String> = emptyList()
        ) = SelectedContentContractDocument(id = ObjectId(), name = name, collectionIds = collectionIds)
    }
}