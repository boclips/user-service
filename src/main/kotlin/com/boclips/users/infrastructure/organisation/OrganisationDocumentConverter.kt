package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import java.time.ZoneOffset
import java.time.ZonedDateTime

object OrganisationDocumentConverter {
    fun fromDocument(organisationDocument: OrganisationDocument): Organisation<*> {
        val organisation = when (organisationDocument.type) {

            OrganisationType.API -> ApiIntegration(
                name = organisationDocument.name,
                country = organisationDocument.country?.let { Country.fromCode(it.code) },
                state = organisationDocument.state?.let { State.fromCode(it.code) },
                allowsOverridingUserIds = organisationDocument.allowsOverridingUserIds ?: false
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

        return Organisation(
            id = OrganisationId(organisationDocument.id!!),
            type = organisationDocument.dealType ?: organisationDocument.parentOrganisation?.dealType
            ?: DealType.STANDARD,
            accessRuleIds = organisationDocument.accessRuleIds.map { AccessRuleId(it) },
            organisation = organisation,
            accessExpiresOn = organisationDocument.accessExpiresOn?.let { ZonedDateTime.ofInstant(it, ZoneOffset.UTC) },
            role = organisationDocument.role
        )
    }

    fun toDocument(organisation: Organisation<*>): OrganisationDocument = OrganisationDocument(
        id = organisation.id.value,
        dealType = organisation.type,
        name = organisation.organisation.name,
        role = organisation.role,
        accessRuleIds = organisation.accessRuleIds.map { it.value },
        externalId = when (organisation.organisation) {
            is School -> organisation.organisation.externalId
            is District -> organisation.organisation.externalId
            is ApiIntegration -> null
        },
        type = organisation.organisation.type(),
        country = organisation.organisation.country?.id?.let { LocationDocument(code = it) },
        state = organisation.organisation.state?.id?.let { LocationDocument(code = it) },
        postcode = organisation.organisation.postcode,
        allowsOverridingUserIds = when (organisation.organisation) {
            is ApiIntegration -> organisation.organisation.allowsOverridingUserIds
            else -> null
        },
        parentOrganisation = when (organisation.organisation) {
            is School -> organisation.organisation.district?.let {
                toDocument(organisation = it)
            }
            else -> null
        },
        accessExpiresOn = organisation.accessExpiresOn?.toInstant()
    )

    private fun mapSchoolDistrict(organisationDocument: OrganisationDocument): Organisation<District>? =
        organisationDocument.parentOrganisation
            ?.let { fromDocument(it) }
            ?.takeIf { it.organisation is District }
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as Organisation<District>
            }
}
