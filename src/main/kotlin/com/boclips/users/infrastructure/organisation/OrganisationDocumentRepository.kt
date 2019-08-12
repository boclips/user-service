package com.boclips.users.infrastructure.organisation

import org.springframework.data.mongodb.repository.MongoRepository

interface OrganisationDocumentRepository : MongoRepository<OrganisationDocument, String> {
    fun findByName(name: String): OrganisationDocument?
}