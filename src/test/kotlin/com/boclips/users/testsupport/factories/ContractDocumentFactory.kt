package com.boclips.users.testsupport.factories

import com.boclips.users.infrastructure.contract.ContractDocument
import org.bson.types.ObjectId

class ContractDocumentFactory {
    companion object {
        fun sampleSelectedCollectionsContractDocument(
            name: String = "Test selected collections contract",
            collectionIds: List<String> = emptyList()
        ) = ContractDocument.SelectedCollections().apply {
            this.id = ObjectId()
            this.name = name
            this.collectionIds = collectionIds
        }

        fun sampleSelectedVideosContractDocument(
            name: String = "Test selected videos contract",
            videoIds: List<String> = emptyList()
        ) = ContractDocument.SelectedVideos().apply {
            this.id = ObjectId()
            this.name = name
            this.videoIds = videoIds
        }
    }
}
