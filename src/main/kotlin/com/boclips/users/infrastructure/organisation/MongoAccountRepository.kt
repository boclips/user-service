package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.accessrules.AccessRuleId
import com.boclips.users.domain.model.account.Organisation
import com.boclips.users.domain.model.account.OrganisationId
import com.boclips.users.domain.model.account.ApiIntegration
import com.boclips.users.domain.model.account.District
import com.boclips.users.domain.model.account.OrganisationDetails
import com.boclips.users.domain.model.account.OrganisationType
import com.boclips.users.domain.model.account.School
import com.boclips.users.domain.service.AccountExpiresOnUpdate
import com.boclips.users.domain.service.AccountRepository
import com.boclips.users.domain.service.AccountTypeUpdate
import com.boclips.users.domain.service.AccountUpdate
import com.boclips.users.infrastructure.organisation.OrganisationDocumentConverter.fromDocument
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import java.time.ZonedDateTime

class MongoAccountRepository(
    private val repository: OrganisationRepository
) :
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

    override fun findApiIntegrationByRole(role: String): Organisation<ApiIntegration>? {
        return repository.findByRoleAndType(role = role, type = OrganisationType.API)
            ?.let {
                @Suppress("UNCHECKED_CAST")
                fromDocument(it) as Organisation<ApiIntegration>
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun save(
        apiIntegration: ApiIntegration,
        accessRuleIds: List<AccessRuleId>,
        role: String?
    ) =
        doSave(role, accessRuleIds, apiIntegration) as Organisation<ApiIntegration>

    @Suppress("UNCHECKED_CAST")
    override fun save(school: School, accessExpiresOn: ZonedDateTime?) =
        doSave(organisationDetails = school, accessExpiresOn = accessExpiresOn) as Organisation<School>

    @Suppress("UNCHECKED_CAST")
    override fun save(district: District, accessExpiresOn: ZonedDateTime?) =
        doSave(organisationDetails = district, accessExpiresOn = accessExpiresOn) as Organisation<District>

    override fun update(update: AccountUpdate): Organisation<*>? {
        val document = repository.findByIdOrNull(update.id.value) ?: return null

        val updatedDocument = when (update) {
            is AccountTypeUpdate -> document.copy(accountType = update.type)
            is AccountExpiresOnUpdate -> document.copy(accessExpiresOn = update.accessExpiresOn.toInstant())
        }

        return fromDocument(repository.save(updatedDocument))
    }

    override fun findAccountsByParentId(parentId: OrganisationId): List<Organisation<*>> {
        return repository.findByParentOrganisationId(parentId.value).map(::fromDocument)
    }

    private fun doSave(
        role: String? = null,
        accessRuleIds: List<AccessRuleId> = emptyList(),
        organisationDetails: OrganisationDetails,
        accessExpiresOn: ZonedDateTime? = null
    ): Organisation<*> {
        return fromDocument(
            repository.save(
                organisationDocument(
                    organisationDetails = organisationDetails,
                    role = role,
                    accessRuleIds = accessRuleIds,
                    accessExpiresOn = accessExpiresOn
                )
            )
        )
    }

    private fun organisationDocument(
        organisationDetails: OrganisationDetails,
        role: String?,
        accessRuleIds: List<AccessRuleId>,
        id: String? = null,
        accessExpiresOn: ZonedDateTime? = null
    ): OrganisationDocument {
        return OrganisationDocument(
            id = id,
            accountType = null,
            name = organisationDetails.name,
            role = role,
            accessRuleIds = accessRuleIds.map { it.value },
            externalId = when (organisationDetails) {
                is School -> organisationDetails.externalId
                is District -> organisationDetails.externalId
                is ApiIntegration -> null
            },
            type = organisationDetails.type(),
            country = organisationDetails.country?.id?.let { LocationDocument(code = it) },
            state = organisationDetails.state?.id?.let { LocationDocument(code = it) },
            postcode = organisationDetails.postcode,
            allowsOverridingUserIds = when (organisationDetails) {
                is ApiIntegration -> organisationDetails.allowsOverridingUserIds
                else -> null
            },
            parentOrganisation = when (organisationDetails) {
                is School -> organisationDetails.district?.let {
                    organisationDocument(
                        organisationDetails = it.organisation,
                        role = null,
                        accessRuleIds = it.accessRuleIds,
                        id = it.id.value
                    )
                }
                else -> null
            },
            accessExpiresOn = accessExpiresOn?.toInstant()
        )
    }

    override fun findAccountById(id: OrganisationId): Organisation<*>? {
        val potentialOrganisationDocument = repository.findById(id.value)
        return if (potentialOrganisationDocument.isPresent) {
            fromDocument(potentialOrganisationDocument.get())
        } else {
            null
        }
    }

    override fun findSchoolById(id: OrganisationId): Organisation<School>? {
        return findAccountById(id)
            ?.takeIf { it.organisation is School }
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

    override fun findAccounts(
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

    override fun findAccountByExternalId(id: String): Organisation<*>? {
        return repository.findByExternalId(id)?.let { fromDocument(it) }
    }
}
