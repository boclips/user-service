package com.boclips.users.infrastructure.user

import org.springframework.data.mongodb.repository.MongoRepository

interface OrganisationDocumentRepository : MongoRepository<OrganisationDocument, String> {
    fun findByName(name: String): OrganisationDocument?
}