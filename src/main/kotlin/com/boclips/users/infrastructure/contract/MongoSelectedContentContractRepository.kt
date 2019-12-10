package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.service.SelectedContentContractRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class MongoSelectedContentContractRepository(
    private val selectedCollectionContractDocumentMongoRepository: SelectedCollectionContractDocumentMongoRepository,
    private val contractDocumentConverter: ContractDocumentConverter
) : SelectedContentContractRepository {
    override fun saveSelectedContentContract(
        name: String,
        collectionIds: List<CollectionId>
    ): Contract.SelectedContent {
        return contractDocumentConverter.fromDocument(
            selectedCollectionContractDocumentMongoRepository.save(
                ContractDocument.SelectedCollections().apply {
                    this.id = ObjectId()
                    this.name = name
                    this.collectionIds = collectionIds.map { it.value }
                }
            )
        ) as Contract.SelectedContent
    }

    override fun saveSelectedCollectionsContract(
        name: String,
        collectionIds: List<CollectionId>
    ): Contract.SelectedContent {
        return contractDocumentConverter.fromDocument(
            selectedCollectionContractDocumentMongoRepository.save(
                ContractDocument.SelectedCollections().apply {
                    this.id = ObjectId()
                    this.name = name
                    this.collectionIds = collectionIds.map { it.value }
                }
            )
        ) as Contract.SelectedContent
    }
}
