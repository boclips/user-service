package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.contract.VideoId
import org.bson.types.ObjectId

class ContractFactory {
    companion object {
        fun sampleSelectedCollectionsContract(
            id: ContractId = ContractId(ObjectId().toHexString()),
            name: String = "Tailored collections list",
            collectionIds: List<CollectionId> = emptyList()
        ) = Contract.SelectedCollections(id, name, collectionIds)

        fun sampleSelectedVideosContract(
            id: ContractId = ContractId(ObjectId().toHexString()),
            name: String = "Tailored videos list",
            videoIds: List<VideoId> = emptyList()
        ) = Contract.SelectedVideos(id, name, videoIds)
    }
}
