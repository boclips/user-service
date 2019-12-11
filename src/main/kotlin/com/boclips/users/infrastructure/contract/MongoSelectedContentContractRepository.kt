package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.VideoId
import com.boclips.users.domain.service.SelectedContentContractRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class MongoSelectedContentContractRepository(
    private val selectedCollectionsContractDocumentMongoRepository: SelectedCollectionsContractDocumentMongoRepository,
    private val selectedVideosContractDocumentMongoRepository: SelectedVideosContractDocumentMongoRepository,
    private val contractDocumentConverter: ContractDocumentConverter
) : SelectedContentContractRepository {
    override fun saveSelectedCollectionsContract(
        name: String,
        collectionIds: List<CollectionId>
    ): Contract.SelectedCollections {
        return contractDocumentConverter.fromDocument(
            selectedCollectionsContractDocumentMongoRepository.save(
                ContractDocument.SelectedCollections().apply {
                    this.id = ObjectId()
                    this.name = name
                    this.collectionIds = collectionIds.map { it.value }
                }
            )
        ) as Contract.SelectedCollections
    }

    override fun saveSelectedVideosContract(name: String, videoIds: List<VideoId>): Contract.SelectedVideos {
        return contractDocumentConverter.fromDocument(
            selectedVideosContractDocumentMongoRepository.save(
                ContractDocument.SelectedVideos().apply {
                    this.id = ObjectId()
                    this.name = name
                    this.videoIds = videoIds.map { it.value }
                }
            )
        ) as Contract.SelectedVideos
    }
}
