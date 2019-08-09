package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.infrastructure.user.OrganisationDocumentConverter.fromDocument
import com.boclips.users.infrastructure.user.OrganisationDocumentConverter.toDocument
import org.springframework.stereotype.Repository

@Repository
class MongoOrganisationRepository(
    private val repository: OrganisationDocumentRepository
) : OrganisationRepository {
    override fun findByName(organisationName: String): Organisation? {
        return repository.findByName(organisationName)?.let { fromDocument(it) }
    }

    override fun save(organisation: Organisation): Organisation {
        return fromDocument(
            repository.save(toDocument(organisation))
        )
    }
}