package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.service.OrganisationAccountRepository
import com.boclips.users.infrastructure.organisation.OrganisationDocumentConverter.fromDocument
import org.springframework.stereotype.Repository

@Repository
class MongoOrganisationAccountRepository(private val repository: OrganisationSpringDataRepository) :
    OrganisationAccountRepository {

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

    override fun findApiIntegrationByRole(role: String): OrganisationAccount<ApiIntegration>? {
        return repository.findByRoleAndType(role = role, type = OrganisationType.API)
            ?.let { fromDocument(it) as OrganisationAccount<ApiIntegration> }
    }

    override fun save(
        apiIntegration: ApiIntegration,
        contractIds: List<ContractId>,
        role: String?
    ) =
        doSave(role, contractIds, apiIntegration) as OrganisationAccount<ApiIntegration>

    override fun save(school: School) =
        doSave(organisation = school) as OrganisationAccount<School>

    override fun save(district: District) =
        doSave(organisation = district) as OrganisationAccount<District>

    private fun doSave(
        role: String? = null,
        contractIds: List<ContractId> = emptyList(),
        organisation: Organisation
    ): OrganisationAccount<*> {
        return fromDocument(
            repository.save(
                organisationDocument(organisation, role, contractIds)
            )
        )
    }

    private fun organisationDocument(
        organisation: Organisation,
        role: String?,
        contractIds: List<ContractId>,
        id: String? = null
    ): OrganisationDocument {
        return OrganisationDocument(
            id = id,
            name = organisation.name,
            role = role,
            contractIds = contractIds.map { it.value },
            externalId = when (organisation) {
                is School -> organisation.externalId
                is District -> organisation.externalId
                is ApiIntegration -> null
            },
            type = organisation.type(),
            country = organisation.country?.id?.let { LocationDocument(code = it) },
            state = organisation.state?.id?.let { LocationDocument(code = it) },
            parentOrganisation = when (organisation) {
                is School -> organisation.district?.let { organisationDocument(
                        organisation = it.organisation,
                        role = null,
                        contractIds = it.contractIds,
                        id = it.id.value
                ) }
                else -> null
            }
        )
    }

    override fun findOrganisationAccountById(id: OrganisationAccountId): OrganisationAccount<*>? {
        val potentialOrganisationDocument = repository.findById(id.value)
        return if (potentialOrganisationDocument.isPresent) {
            fromDocument(potentialOrganisationDocument.get())
        } else {
            null
        }
    }

    override fun findSchoolById(id: OrganisationAccountId): OrganisationAccount<School>? {
        return findOrganisationAccountById(id)
            ?.takeIf { it.organisation is School }
            ?.let { it as OrganisationAccount<School> }
    }

    override fun findSchools(): List<OrganisationAccount<School>> {
        return repository.findByType(OrganisationType.SCHOOL).toList()
            .map { fromDocument(it) as OrganisationAccount<School> }
    }

    override fun findApiIntegrationByName(name: String): OrganisationAccount<ApiIntegration>? {
        return repository.findByNameAndType(name = name, type = OrganisationType.API)
            ?.let { fromDocument(it) as OrganisationAccount<ApiIntegration> }
    }

    override fun findOrganisationAccountByExternalId(id: String): OrganisationAccount<*>? {
        return repository.findByExternalId(id)?.let { fromDocument(it) }
    }
}
