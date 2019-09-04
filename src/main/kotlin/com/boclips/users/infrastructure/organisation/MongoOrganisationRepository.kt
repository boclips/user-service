package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.infrastructure.organisation.OrganisationDocumentConverter.fromDocument
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class MongoOrganisationRepository(private val repository: OrganisationSpringDataRepository) : OrganisationRepository {
    override fun findByType(organisationType: OrganisationType): List<Organisation> {
        return when (organisationType) {
            is OrganisationType.District -> repository.findByExternalIdNotNull().toList().map { fromDocument(it) }
            is OrganisationType.ApiCustomer -> repository.findByType(type = "API").toList().map { fromDocument(it) }
            is OrganisationType.School -> repository.findByType(type = "SCHOOL").toList().map { fromDocument(it) }
        }
    }

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
        districtId: String?,
        organisationType: OrganisationType?,
        countryId: String?,
        stateId: String?
    ): Organisation {
        return fromDocument(
            repository.save(
                OrganisationDocument(
                    id = ObjectId(),
                    name = organisationName,
                    role = role,
                    contractIds = contractIds.map { it.value },
                    externalId = districtId,
                    type = when (organisationType) {
                        OrganisationType.ApiCustomer -> "API"
                        OrganisationType.District -> "DISTRICT"
                        OrganisationType.School -> "SCHOOL"
                        else -> null
                    },
                    country = countryId,
                    state = stateId
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
