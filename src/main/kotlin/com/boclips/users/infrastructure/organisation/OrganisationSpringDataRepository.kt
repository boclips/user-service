package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.OrganisationType
import org.springframework.data.mongodb.repository.MongoRepository

interface OrganisationSpringDataRepository : MongoRepository<OrganisationDocument, String> {
    fun findByNameAndType(name: String, type: OrganisationType): OrganisationDocument?
    fun findByRoleAndType(role: String, type: OrganisationType): OrganisationDocument?
    fun findByTypeAndCountryCodeAndNameContainsIgnoreCase(
        type: OrganisationType,
        code: String,
        name: String
    ): Iterable<OrganisationDocument>
    fun findByType(type: OrganisationType): List<OrganisationDocument>
    fun findByExternalId(id: String): OrganisationDocument?
    fun findByParentOrganisationId(ParentOrganisationId: String): List<OrganisationDocument>
}
