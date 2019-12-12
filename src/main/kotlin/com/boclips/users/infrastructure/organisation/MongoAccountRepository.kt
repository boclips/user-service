package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.account.ApiIntegration
import com.boclips.users.domain.model.account.District
import com.boclips.users.domain.model.account.Organisation
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.OrganisationAccountId
import com.boclips.users.domain.model.account.OrganisationType
import com.boclips.users.domain.model.account.School
import com.boclips.users.domain.service.AccountExpiresOnUpdate
import com.boclips.users.domain.service.AccountRepository
import com.boclips.users.domain.service.AccountTypeUpdate
import com.boclips.users.domain.service.AccountUpdate
import com.boclips.users.infrastructure.organisation.OrganisationDocumentConverter.fromDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import java.time.ZonedDateTime

class MongoAccountRepository(private val repository: OrganisationSpringDataRepository) :
    AccountRepository {

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

    override fun findApiIntegrationByRole(role: String): Account<ApiIntegration>? {
        return repository.findByRoleAndType(role = role, type = OrganisationType.API)
            ?.let {
                @Suppress("UNCHECKED_CAST")
                fromDocument(it) as Account<ApiIntegration>
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun save(
        apiIntegration: ApiIntegration,
        contractIds: List<ContractId>,
        role: String?
    ) =
        doSave(role, contractIds, apiIntegration) as Account<ApiIntegration>

    @Suppress("UNCHECKED_CAST")
    override fun save(school: School, accessExpiresOn: ZonedDateTime?) =
        doSave(organisation = school, accessExpiresOn = accessExpiresOn) as Account<School>

    @Suppress("UNCHECKED_CAST")
    override fun save(district: District, accessExpiresOn: ZonedDateTime?) =
        doSave(organisation = district, accessExpiresOn = accessExpiresOn) as Account<District>

    override fun update(update: AccountUpdate): Account<*>? {
        val document = repository.findByIdOrNull(update.id.value) ?: return null

        val updatedDocument = when (update) {
            is AccountTypeUpdate -> document.copy(accountType = update.type)
            is AccountExpiresOnUpdate -> document.copy(accessExpiresOn = update.accessExpiresOn.toInstant())
        }

        return fromDocument(repository.save(updatedDocument))
    }

    override fun findOrganisationAccountsByParentId(parentId: OrganisationAccountId): List<Account<*>> {
        return repository.findByParentOrganisationId(parentId.value).map { fromDocument(it) }
    }

    private fun doSave(
        role: String? = null,
        contractIds: List<ContractId> = emptyList(),
        organisation: Organisation,
        accessExpiresOn: ZonedDateTime? = null
    ): Account<*> {
        return fromDocument(
            repository.save(
                organisationDocument(
                    organisation = organisation,
                    role = role,
                    contractIds = contractIds,
                    accessExpiresOn = accessExpiresOn
                )
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

    override fun findOrganisationAccountById(id: OrganisationAccountId): Account<*>? {
        val potentialOrganisationDocument = repository.findById(id.value)
        return if (potentialOrganisationDocument.isPresent) {
            fromDocument(potentialOrganisationDocument.get())
        } else {
            null
        }
    }

    override fun findSchoolById(id: OrganisationAccountId): Account<School>? {
        return findOrganisationAccountById(id)
            ?.takeIf { it.organisation is School }
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as Account<School>
            }
    }

    override fun findSchools(): List<Account<School>> {
        return repository.findByType(OrganisationType.SCHOOL).toList()
            .map {
                @Suppress("UNCHECKED_CAST")
                fromDocument(it) as Account<School>
            }
    }

    override fun findIndependentSchoolsAndDistricts(searchRequest: AccountSearchRequest): Page<Account<*>>? {
        return repository.findByCountryCodeAndParentOrganisationIsNullAndTypeIsNotOrderByAccessExpiresOnDescNameAsc(
            searchRequest.countryCode,
            OrganisationType.API,
            PageRequest.of(searchRequest.page ?: 0, searchRequest.size ?: 30)
        ).map {
            @Suppress("UNCHECKED_CAST")
            fromDocument(it) as Account<Organisation>
        }
    }

    override fun findApiIntegrationByName(name: String): Account<ApiIntegration>? {
        return repository.findByNameAndType(name = name, type = OrganisationType.API)
            ?.let {
                @Suppress("UNCHECKED_CAST")
                fromDocument(it) as Account<ApiIntegration>
            }
    }

    override fun findOrganisationAccountByExternalId(id: String): Account<*>? {
        return repository.findByExternalId(id)?.let { fromDocument(it) }
    }
}
