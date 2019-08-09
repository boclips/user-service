package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.infrastructure.user.OrganisationDocumentConverter.fromDocument
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class MongoOrganisationRepository(
    private val repository: OrganisationDocumentRepository
) : OrganisationRepository {
    override fun findByName(organisationName: String): Organisation? {
        return repository.findByName(organisationName)?.let { fromDocument(it) }
    }

    override fun save(organisationName: String): Organisation {
        return fromDocument(
            repository.save(
                OrganisationDocument(
                    id = ObjectId(),
                    name = organisationName
                )
            )
        )
    }
}