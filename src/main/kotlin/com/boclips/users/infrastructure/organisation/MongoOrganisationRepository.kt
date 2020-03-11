package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationDetails
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.service.OrganisationDomainOnUpdate
import com.boclips.users.domain.service.OrganisationExpiresOnUpdate
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.domain.service.OrganisationTypeUpdate
import com.boclips.users.domain.service.OrganisationUpdate
import com.boclips.users.infrastructure.organisation.OrganisationDocumentConverter.fromDocument
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull

class MongoOrganisationRepository(
    private val repository: SpringDataMongoOrganisationRepository
) : OrganisationRepository {

    override fun lookupSchools(
        schoolName: String,
        countryCode: String
    ): List<LookupEntry> {
        return repository.findByTypeAndCountryCodeAndNameContainsIgnoreCase(
            code = countryCode,
            name = schoolName,
            type = OrganisationType.SCHOOL
        ).toList().map { LookupEntry("${it.id}", it.name) }
    }

    override fun findApiIntegrationByRole(role: String): Organisation<ApiIntegration>? {
        return repository.findByRoleAndType(role = role, type = OrganisationType.API)
            ?.let {
                @Suppress("UNCHECKED_CAST")
                fromDocument(it) as Organisation<ApiIntegration>
            }
    }

    override fun <T : OrganisationDetails> save(organisation: Organisation<T>): Organisation<T> {
        return repository.save(OrganisationDocumentConverter.toDocument(organisation)).let {
            @Suppress("UNCHECKED_CAST")
            fromDocument(it) as Organisation<T>
        }
    }

    override fun update(update: OrganisationUpdate): Organisation<*>? {
        val document = repository.findByIdOrNull(update.id.value) ?: return null

        val updatedDocument = when (update) {
            is OrganisationTypeUpdate -> document.copy(dealType = update.type)
            is OrganisationExpiresOnUpdate -> document.copy(accessExpiresOn = update.accessExpiresOn.toInstant())
            is OrganisationDomainOnUpdate -> document.copy(domain = update.domain)
        }

        return fromDocument(repository.save(updatedDocument))
    }

    override fun findOrganisationsByParentId(parentId: OrganisationId): List<Organisation<*>> {
        return repository.findByParentOrganisationId(parentId.value).map(::fromDocument)
    }

    override fun findOrganisationById(id: OrganisationId): Organisation<*>? {
        val potentialOrganisationDocument = repository.findById(id.value)
        return if (potentialOrganisationDocument.isPresent) {
            fromDocument(potentialOrganisationDocument.get())
        } else {
            null
        }
    }

    override fun findSchoolById(id: OrganisationId): Organisation<School>? {
        return findOrganisationById(id)
            ?.takeIf { it.details is School }
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as Organisation<School>
            }
    }

    override fun findSchools(): List<Organisation<School>> {
        return repository.findByType(OrganisationType.SCHOOL).toList()
            .map {
                @Suppress("UNCHECKED_CAST")
                fromDocument(it) as Organisation<School>
            }
    }

    override fun findOrganisations(
        countryCode: String?, types: List<OrganisationType>?, page: Int, size: Int
    ): Page<Organisation<*>>? {
        val results =
            repository.findOrganisations(
                OrganisationSearchRequest(
                    countryCode = countryCode,
                    organisationTypes = types,
                    parentOnly = true,
                    page = page,
                    size = size
                )
            )

        return results.map {
            @Suppress("UNCHECKED_CAST")
            fromDocument(it) as Organisation<OrganisationDetails>
        }
    }

    override fun findApiIntegrationByName(name: String): Organisation<ApiIntegration>? {
        return repository.findByNameAndType(name = name, type = OrganisationType.API)
            ?.let {
                @Suppress("UNCHECKED_CAST")
                fromDocument(it) as Organisation<ApiIntegration>
            }
    }

    override fun findOrganisationByExternalId(id: String): Organisation<*>? {
        return repository.findByExternalId(id)?.let { fromDocument(it) }
    }
}
