package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.contentpackage.ContentPackageId
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.mongodb.DBRef
import org.bson.types.ObjectId
import java.time.ZoneOffset
import java.time.ZonedDateTime

object OrganisationDocumentConverter {
    fun fromDocument(organisationDocument: OrganisationDocument, parentOrganisationDocument: OrganisationDocument?): Organisation<*> {
        val organisation = when (organisationDocument.type) {
            OrganisationType.API -> ApiIntegration(
                name = organisationDocument.name,
                domain = organisationDocument.domain,
                country = organisationDocument.country?.let { Country.fromCode(it.code) },
                state = organisationDocument.state?.let { State.fromCode(it.code) },
                allowsOverridingUserIds = organisationDocument.allowsOverridingUserIds ?: false
            )

            OrganisationType.SCHOOL -> School(
                name = organisationDocument.name,
                domain = organisationDocument.domain,
                country = organisationDocument.country?.let { Country.fromCode(it.code) }
                    ?: throw IllegalStateException("School ${organisationDocument._id} must have a country"),
                state = organisationDocument.state?.let { State.fromCode(it.code) },
                postcode = organisationDocument.postcode,
                district = parentOrganisationDocument?.let(this::mapSchoolDistrict),
                externalId = organisationDocument.externalId
            )

            OrganisationType.DISTRICT -> District(
                name = organisationDocument.name,
                domain = organisationDocument.domain,
                state = organisationDocument.state?.let { State.fromCode(it.code) }
                    ?: throw IllegalStateException("District ${organisationDocument._id} must have a state"),
                externalId = organisationDocument.externalId
                    ?: throw IllegalStateException("District ${organisationDocument._id} must have externalId")

            )
        }

        return Organisation(
            id = OrganisationId(organisationDocument._id!!.toHexString()),
            type = organisationDocument.dealType ?: parentOrganisationDocument?.dealType ?: DealType.STANDARD,
            details = organisation,
            accessExpiresOn = organisationDocument.accessExpiresOn?.let { ZonedDateTime.ofInstant(it, ZoneOffset.UTC) },
            role = organisationDocument.role,
            contentPackageId = organisationDocument.contentPackageId?.let { ContentPackageId(value = it) }
        )
    }

    fun toDocument(organisation: Organisation<*>): OrganisationDocuments {

        val district = when (organisation.details) {
            is School -> organisation.details.district
            else -> null
        }

        val parentOrganisationDocument = district
            ?.let { d -> this.toDocument(d).organisation }

        val organisationDocument = OrganisationDocument(
            _id = ObjectId(organisation.id.value),
            dealType = organisation.type,
            name = organisation.details.name,
            domain = organisation.details.domain,
            role = organisation.role,
            externalId = when (organisation.details) {
                is School -> organisation.details.externalId
                is District -> organisation.details.externalId
                is ApiIntegration -> null
            },
            type = organisation.details.type(),
            country = organisation.details.country?.id?.let { LocationDocument(code = it) },
            state = organisation.details.state?.id?.let { LocationDocument(code = it) },
            postcode = organisation.details.postcode,
            allowsOverridingUserIds = when (organisation.details) {
                is ApiIntegration -> organisation.details.allowsOverridingUserIds
                else -> null
            },
            parentOrganisation = district?.let {
                DBRef("organisations", ObjectId(it.id.value))
            },
            accessExpiresOn = organisation.accessExpiresOn?.toInstant(),
            contentPackageId = organisation.contentPackageId?.value
        )

        return OrganisationDocuments(organisationDocument, parentOrganisationDocument)
    }

    private fun mapSchoolDistrict(parentOrganisationDocument: OrganisationDocument): Organisation<District>? {
        val organisation = fromDocument(parentOrganisationDocument, parentOrganisationDocument = null)
        @Suppress("UNCHECKED_CAST")
        return if(organisation.details is District) organisation as Organisation<District> else null
    }
}

data class OrganisationDocuments(val organisation: OrganisationDocument, val parent: OrganisationDocument?)
