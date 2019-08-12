package com.boclips.users.infrastructure.organisation

import org.springframework.data.mongodb.repository.MongoRepository

interface OrganisationSpringDataRepository : MongoRepository<OrganisationDocument, String> {
    fun findByName(name: String): OrganisationDocument?
}