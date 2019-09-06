package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.service.OrganisationAccountRepository
import com.boclips.users.infrastructure.organisation.OrganisationDocumentConverter.fromDocument
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class MongoOrganisationAccountRepository(private val repository: OrganisationSpringDataRepository) :
    OrganisationAccountRepository {

    override fun lookupSchools(
        schoolName: String,
        country: String
    ): List<LookupEntry> {
        return repository.findByTypeAndCountryCodeAndNameContainsIgnoreCase(
            code = country,
            name = schoolName,
            type = OrganisationType.SCHOOL
        ).toList().map { LookupEntry("${it.id}", it.name) }
    }

    override fun findDistricts(): List<OrganisationAccount> {
        return repository.findByExternalIdNotNull().toList().map { fromDocument(it) }
    }

    override fun findApiIntegrationByRole(role: String): OrganisationAccount? {
        return repository.findByRoleAndType(role = role, type = OrganisationType.API)?.let { fromDocument(it) }
    }

    override fun save(
        role: String?,
        contractIds: List<ContractId>,
        apiIntegration: ApiIntegration
    ) =
        doSave(role, contractIds, apiIntegration)

    override fun save(school: School) =
        doSave(organisation = school)

    override fun save(district: District) =
        doSave(organisation = district)

    private fun doSave(
        role: String? = null,
        contractIds: List<ContractId> = emptyList(),
        organisation: Organisation
    ): OrganisationAccount {
        return fromDocument(
            repository.save(
                OrganisationDocument(
                    id = ObjectId(),
                    name = organisation.name,
                    role = role,
                    contractIds = contractIds.map { it.value },
                    externalId = when (organisation) {
                        is School -> organisation.externalId
                        is District -> organisation.externalId
                        is ApiIntegration -> null
                    },
                    type = when (organisation) {
                        is District -> OrganisationType.DISTRICT
                        is School -> OrganisationType.SCHOOL
                        is ApiIntegration -> OrganisationType.API
                    },
                    country = organisation.country?.id?.let { LocationDocument(code = it) },
                    state = organisation.state?.id?.let { LocationDocument(code = it) }
                )
            )
        )
    }

    override fun findOrganisationAccountById(id: OrganisationAccountId): OrganisationAccount? {
        val potentialOrganisationDocument = repository.findById(id.value)
        return if (potentialOrganisationDocument.isPresent) {
            fromDocument(potentialOrganisationDocument.get())
        } else {
            null
        }
    }

    override fun findApiIntegrationByName(name: String): OrganisationAccount? {
        return repository.findByNameAndType(name = name, type = OrganisationType.API)?.let { fromDocument(it) }
    }
}
