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
import com.boclips.users.domain.service.OrganisationAccountExpiresOnUpdate
import com.boclips.users.domain.service.OrganisationAccountRepository
import com.boclips.users.domain.service.OrganisationAccountTypeUpdate
import com.boclips.users.domain.service.OrganisationAccountUpdate
import com.boclips.users.infrastructure.organisation.OrganisationDocumentConverter.fromDocument
import org.springframework.data.repository.findByIdOrNull
import java.time.ZonedDateTime

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
            ?.let {
                @Suppress("UNCHECKED_CAST")
                fromDocument(it) as OrganisationAccount<ApiIntegration>
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun save(
        apiIntegration: ApiIntegration,
        contractIds: List<ContractId>,
        role: String?
    ) =
        doSave(role, contractIds, apiIntegration) as OrganisationAccount<ApiIntegration>

    @Suppress("UNCHECKED_CAST")
    override fun save(school: School, accessExpiresOn: ZonedDateTime?) =
        doSave(organisation = school, accessExpiresOn = accessExpiresOn) as OrganisationAccount<School>

    @Suppress("UNCHECKED_CAST")
    override fun save(district: District, accessExpiresOn: ZonedDateTime?) =
        doSave(organisation = district, accessExpiresOn = accessExpiresOn) as OrganisationAccount<District>

    override fun update(update: OrganisationAccountUpdate): OrganisationAccount<*>? {
        val document = repository.findByIdOrNull(update.id.value) ?: return null

        val updatedDocument = when(update) {
            is OrganisationAccountTypeUpdate -> document.copy(accountType = update.type)
            is OrganisationAccountExpiresOnUpdate -> document.copy(accessExpiresOn = update.accessExpiresOn.toInstant())
        }

        return fromDocument(repository.save(updatedDocument))
    }

    override fun findOrganisationAccountsByParentId(parentId: OrganisationAccountId): List<OrganisationAccount<*>> {
        return repository.findByParentOrganisationId(parentId.value).map { fromDocument(it) }
    }

    private fun doSave(
        role: String? = null,
        contractIds: List<ContractId> = emptyList(),
        organisation: Organisation,
        accessExpiresOn: ZonedDateTime? = null
    ): OrganisationAccount<*> {
        return fromDocument(
            repository.save(
                organisationDocument(organisation = organisation, role = role, contractIds = contractIds, accessExpiresOn = accessExpiresOn)
            )
        )
    }

    private fun organisationDocument(
        organisation: Organisation,
        role: String?,
        contractIds: List<ContractId>,
        id: String? = null,
        accessExpiresOn: ZonedDateTime? = null
    ): OrganisationDocument {
        return OrganisationDocument(
            id = id,
            accountType = null,
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
            postcode = organisation.postcode,
            parentOrganisation = when (organisation) {
                is School -> organisation.district?.let {
                    organisationDocument(
                        organisation = it.organisation,
                        role = null,
                        contractIds = it.contractIds,
                        id = it.id.value
                    )
                }
                else -> null
            },
            accessExpiresOn = accessExpiresOn?.toInstant()
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
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as OrganisationAccount<School>
            }
    }

    override fun findSchools(): List<OrganisationAccount<School>> {
        return repository.findByType(OrganisationType.SCHOOL).toList()
            .map {
                @Suppress("UNCHECKED_CAST")
                fromDocument(it) as OrganisationAccount<School>
            }
    }

    override fun findIndependentSchoolsAndDistricts(countryCode: String): List<OrganisationAccount<Organisation>>? {
        return repository.findByCountryCodeAndParentOrganisationIsNullAndTypeIsNot(countryCode, OrganisationType.API).toList()
            .map {
                @Suppress("UNCHECKED_CAST")
                fromDocument(it) as OrganisationAccount<Organisation>
            }
    }

    override fun findApiIntegrationByName(name: String): OrganisationAccount<ApiIntegration>? {
        return repository.findByNameAndType(name = name, type = OrganisationType.API)
            ?.let {
                @Suppress("UNCHECKED_CAST")
                fromDocument(it) as OrganisationAccount<ApiIntegration>
            }
    }

    override fun findOrganisationAccountByExternalId(id: String): OrganisationAccount<*>? {
        return repository.findByExternalId(id)?.let { fromDocument(it) }
    }
}
