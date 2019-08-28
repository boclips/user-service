package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.service.SelectedContentContractRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class MongoSelectedContentContractRepository(
    private val selectedContentContractDocumentMongoRepository: SelectedContentContractDocumentMongoRepository,
    private val contractDocumentConverter: ContractDocumentConverter
) : SelectedContentContractRepository {
    override fun saveSelectedContentContract(
        name: String,
        collectionIds: List<CollectionId>
    ): Contract.SelectedContent {
        return contractDocumentConverter.fromDocument(
            selectedContentContractDocumentMongoRepository.save(
                ContractDocument.SelectedContent(
                    id = ObjectId(),
                    name = name,
                    collectionIds = collectionIds.map { it.value })
            )
        ) as Contract.SelectedContent
    }
}
