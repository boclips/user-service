package com.boclips.users.infrastructure.contract

import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.service.ContractRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class MongoContractRepository(
    private val contractDocumentMongoRepository: ContractDocumentMongoRepository,
    private val contractDocumentConverter: ContractDocumentConverter
) : ContractRepository {
    override fun findById(id: ContractId): Contract? {
        return asNullable(contractDocumentMongoRepository.findById(id.value))?.let {
            return when (it) {
                is ContractDocument.SelectedContent -> contractDocumentConverter.fromDocument(it)
            }
        }
    }

    override fun findAll(): List<Contract> {
        return contractDocumentMongoRepository.findAll().map(contractDocumentConverter::fromDocument)
    }

    override fun findAllByName(name: String): List<Contract> {
        return contractDocumentMongoRepository.findByName(name).map(contractDocumentConverter::fromDocument)
    }

    private fun asNullable(potentialDocument: Optional<ContractDocument>): ContractDocument? {
        return if (potentialDocument.isPresent) {
            potentialDocument.get()
        } else {
            null
        }
    }
}