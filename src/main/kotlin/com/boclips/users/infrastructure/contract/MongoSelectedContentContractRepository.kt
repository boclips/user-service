package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.contract.SelectedContentContract
import com.boclips.users.domain.service.SelectedContentContractRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class MongoSelectedContentContractRepository(
    private val selectedContentContractDocumentMongoRepository: SelectedContentContractDocumentMongoRepository,
    private val selectedContentContractDocumentConverter: SelectedContentContractDocumentConverter
) : SelectedContentContractRepository {
    override fun saveSelectedContentContract(
        name: String,
        collectionIds: List<CollectionId>
    ): SelectedContentContract {
        return selectedContentContractDocumentConverter.fromDocument(
            selectedContentContractDocumentMongoRepository.save(
                SelectedContentContractDocument(
                    id = ObjectId(),
                    name = name,
                    collectionIds = collectionIds.map { it.value })
            )
        )
    }

    override fun findById(id: ContractId): SelectedContentContract? {
        val potentialDocument = selectedContentContractDocumentMongoRepository.findById(id.value)
        return if (potentialDocument.isPresent) {
            selectedContentContractDocumentConverter.fromDocument(potentialDocument.get())
        } else {
            null
        }
    }
}
