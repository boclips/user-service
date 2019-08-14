package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.infrastructure.organisation.OrganisationDocumentConverter.fromDocument
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class MongoOrganisationRepository(private val repository: OrganisationSpringDataRepository) : OrganisationRepository {
    override fun findByRole(role: String): Organisation? {
        return repository.findByRole(role)?.let { fromDocument(it) }
    }

    override fun save(organisationName: String, role: String?): Organisation {
        return fromDocument(
            repository.save(
                OrganisationDocument(
                    id = ObjectId(),
                    name = organisationName,
                    role = role
                )
            )
        )
    }
}
