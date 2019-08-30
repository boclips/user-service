package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.infrastructure.organisation.OrganisationDocumentConverter.fromDocument
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class MongoOrganisationRepository(private val repository: OrganisationSpringDataRepository) : OrganisationRepository {
    override fun findByDistrictId(districtId: String): Organisation? {
        return repository.findByExternalId(districtId)?.let { fromDocument(it) }
    }

    override fun findByRole(role: String): Organisation? {
        return repository.findByRole(role)?.let { fromDocument(it) }
    }

    override fun save(
        organisationName: String,
        role: String?,
        contractIds: List<ContractId>,
        districtId: String?
    ): Organisation {
        return fromDocument(
            repository.save(
                OrganisationDocument(
                    id = ObjectId(),
                    name = organisationName,
                    role = role,
                    contractIds = contractIds.map { it.value },
                    externalId = districtId
                )
            )
        )
    }

    override fun findById(id: OrganisationId): Organisation? {
        val potentialOrganisationDocument = repository.findById(id.value)
        return if (potentialOrganisationDocument.isPresent) {
            fromDocument(potentialOrganisationDocument.get())
        } else {
            null
        }
    }
}
