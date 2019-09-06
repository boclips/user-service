package com.boclips.users.infrastructure.organisation

import org.springframework.data.mongodb.repository.MongoRepository

interface OrganisationSpringDataRepository : MongoRepository<OrganisationDocument, String> {
    fun findByNameAndType(name: String, type: OrganisationType): OrganisationDocument?
    fun findByRoleAndType(role: String, type: OrganisationType): OrganisationDocument?
    fun findByExternalIdNotNull(): Iterable<OrganisationDocument>
    fun findByTypeAndCountryCodeAndNameContainsIgnoreCase(
        type: OrganisationType,
        code: String,
        name: String
    ): Iterable<OrganisationDocument>
}
