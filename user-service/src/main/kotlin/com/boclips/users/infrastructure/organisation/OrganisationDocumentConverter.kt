package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import org.bson.types.ObjectId
import java.time.ZoneOffset
import java.time.ZonedDateTime

object OrganisationDocumentConverter {
    fun fromDocument(organisationDocument: OrganisationDocument): Organisation {
        val id = OrganisationId(organisationDocument._id!!.toHexString())

        val address = Address(
            country = organisationDocument.country?.let { Country.fromCode(it.code) },
            state = organisationDocument.state?.let { State.fromCode(it.code) },
            postcode = organisationDocument.postcode
        )

        val deal = Deal(
            contentPackageId = organisationDocument.contentPackageId?.let { ContentPackageId(value = it) },
            accessExpiresOn = organisationDocument.accessExpiresOn?.let { ZonedDateTime.ofInstant(it, ZoneOffset.UTC) },
            type = organisationDocument.dealType ?: organisationDocument.parent?.dealType ?: DealType.STANDARD
        )

        val externalId = organisationDocument.externalId?.let(::ExternalOrganisationId)

        return when (organisationDocument.type) {
            OrganisationType.API -> ApiIntegration(
                id = id,
                name = organisationDocument.name,
                address = address,
                deal = deal,
                domain = organisationDocument.domain,
                allowsOverridingUserIds = organisationDocument.allowsOverridingUserIds ?: false,
                role = organisationDocument.role
            )

            OrganisationType.SCHOOL -> School(
                id = id,
                name = organisationDocument.name,
                domain = organisationDocument.domain,
                address = address,
                deal = deal,
                district = organisationDocument.parent?.let { fromDocument(it) as? District? },
                externalId = externalId,
                role = organisationDocument.role
            )

            OrganisationType.DISTRICT -> District(
                id = id,
                name = organisationDocument.name,
                address = address,
                deal = deal,
                domain = organisationDocument.domain,
                externalId = externalId,
                role = organisationDocument.role
            )
        }

    }

    fun toDocument(organisation: Organisation): OrganisationDocument {
        val district = when (organisation) {
            is School -> organisation.district
            else -> null
        }

        return OrganisationDocument(
            _id = ObjectId(organisation.id.value),
            dealType = organisation.deal.type,
            name = organisation.name,
            domain = organisation.domain,
            role = organisation.role,
            externalId = when (organisation) {
                is School -> organisation.externalId?.value
                is District -> organisation.externalId?.value
                is ApiIntegration -> null
            },
            type = organisation.type(),
            country = organisation.address.country?.id?.let { LocationDocument(code = it) },
            state = organisation.address.state?.id?.let { LocationDocument(code = it) },
            postcode = organisation.address.postcode,
            allowsOverridingUserIds = (organisation as? ApiIntegration?)?.allowsOverridingUserIds,
            parent = district?.let { toDocument(it) },
            accessExpiresOn = organisation.deal.accessExpiresOn?.toInstant(),
            contentPackageId = organisation.deal.contentPackageId?.value
        )
    }
}

