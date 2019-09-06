package com.boclips.users.infrastructure.organisation

import org.springframework.data.mongodb.repository.MongoRepository

interface OrganisationSpringDataRepository : MongoRepository<OrganisationDocument, String> {
    fun findByName(name: String): OrganisationDocument?
    fun findByRole(role: String): OrganisationDocument?
    fun findByExternalId(externalId: String): OrganisationDocument?
    fun findByExternalIdNotNull(): Iterable<OrganisationDocument>
    fun findByTypeAndCountryCodeAndNameContains(
        type: OrganisationType,
        code: String,
        name: String
    ): Iterable<OrganisationDocument>
}
