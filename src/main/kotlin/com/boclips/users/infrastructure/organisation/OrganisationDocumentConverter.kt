package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State

object OrganisationDocumentConverter {
    fun fromDocument(organisationDocument: OrganisationDocument): OrganisationAccount {
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
                district = null,
                externalId = organisationDocument.externalId
            )

            OrganisationType.DISTRICT -> District(
                name = organisationDocument.name,
                state = organisationDocument.state?.let { State.fromCode(it.code) }
                    ?: throw IllegalStateException("District ${organisationDocument.id} must have a state"),
                externalId = organisationDocument.externalId
                    ?: throw IllegalStateException("District ${organisationDocument.id} must have externalId"),
                schools = mapSchoolsFromDistrict(organisationDocument)
            )

        }

        return OrganisationAccount(
            id = OrganisationAccountId(organisationDocument.id.toHexString()),
            contractIds = organisationDocument.contractIds.map { ContractId(it) },
            organisation = organisation
        )
    }

    private fun mapSchoolsFromDistrict(organisationDocument: OrganisationDocument): List<School> {
        return organisationDocument.organisations.map {
            val organisation = fromDocument(it).organisation
            if (organisation is School) {
                return@map organisation as School
            } else {
                throw java.lang.IllegalStateException("Found nested organisation name=${organisation.name} inside district id=${organisationDocument.id} that is not a school. Only schools are supported at the moment.")
            }
        }
    }
}
