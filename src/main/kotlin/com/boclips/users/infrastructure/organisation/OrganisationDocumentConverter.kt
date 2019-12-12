package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.account.ApiIntegration
import com.boclips.users.domain.model.account.District
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.OrganisationAccountId
import com.boclips.users.domain.model.account.AccountType
import com.boclips.users.domain.model.account.OrganisationType
import com.boclips.users.domain.model.account.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import java.time.ZoneOffset
import java.time.ZonedDateTime

object OrganisationDocumentConverter {
    fun fromDocument(organisationDocument: OrganisationDocument): Account<*> {
        val organisation = when (organisationDocument.type) {

            OrganisationType.API -> ApiIntegration(
                name = organisationDocument.name,
                country = organisationDocument.country?.let { Country.fromCode(it.code) },
                state = organisationDocument.state?.let { State.fromCode(it.code) }
            )

            OrganisationType.SCHOOL -> School(
                name = organisationDocument.name,
                country = organisationDocument.country?.let { Country.fromCode(it.code) }
                    ?: throw IllegalStateException("School ${organisationDocument.id} must have a country"),
                state = organisationDocument.state?.let { State.fromCode(it.code) },
                postcode = organisationDocument.postcode,
                district = mapSchoolDistrict(organisationDocument),
                externalId = organisationDocument.externalId
            )

            OrganisationType.DISTRICT -> District(
                name = organisationDocument.name,
                state = organisationDocument.state?.let { State.fromCode(it.code) }
                    ?: throw IllegalStateException("District ${organisationDocument.id} must have a state"),
                externalId = organisationDocument.externalId
                    ?: throw IllegalStateException("District ${organisationDocument.id} must have externalId")

            )
        }

        return Account(
            id = OrganisationAccountId(organisationDocument.id!!),
            type = organisationDocument.accountType ?: organisationDocument.parentOrganisation?.accountType ?: AccountType.STANDARD,
            contractIds = organisationDocument.contractIds.map { ContractId(it) },
            organisation = organisation,
            accessExpiresOn = organisationDocument.accessExpiresOn?.let { ZonedDateTime.ofInstant(it, ZoneOffset.UTC)}
        )
    }

    private fun mapSchoolDistrict(organisationDocument: OrganisationDocument): Account<District>? =
        organisationDocument.parentOrganisation
            ?.let { fromDocument(it) }
            ?.takeIf { it.organisation is District }
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as Account<District>
            }
}
