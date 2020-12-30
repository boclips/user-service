package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.LtiDeployment
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationTag
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.organisation.VideoTypePrices
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import mu.KLogging
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.ZoneOffset
import java.time.ZonedDateTime

object OrganisationDocumentConverter : KLogging() {
    fun fromDocument(organisationDocument: OrganisationDocument): Organisation {
        val id = OrganisationId(organisationDocument._id!!.toHexString())

        val address = Address(
            country = organisationDocument.country?.let { Country.fromCode(it.code) },
            state = organisationDocument.state?.let { State.fromCode(it.code) },
            postcode = organisationDocument.postcode
        )

        val deal = Deal(
            contentPackageId = organisationDocument.contentPackageId?.let { ContentPackageId(value = it) },
            billing = organisationDocument.billing ?: false,
            accessExpiresOn = organisationDocument.accessExpiresOn?.let { ZonedDateTime.ofInstant(it, ZoneOffset.UTC) },
            prices = organisationDocument.prices?.let {
                VideoTypePrices(
                    instructional = it[VideoTypeKey.INSTRUCTIONAL],
                    news = it[VideoTypeKey.NEWS],
                    stock = it[VideoTypeKey.STOCK]
                )
            }
        )

        val tags = organisationDocument.tags.orEmpty().mapNotNull { tagName ->
            try {
                OrganisationTag.valueOf(tagName)
            } catch (_: IllegalArgumentException) {
                logger.error { "Unrecognised tag [$tagName] on organisation ${id.value}" }
                null
            }
        }.toSet()

        val externalId = organisationDocument.externalId?.let(::ExternalOrganisationId)

        val features = organisationDocument.features?.mapKeys { FeatureDocumentConverter.fromDocument(it.key) }

        return when (organisationDocument.type) {
            OrganisationType.API -> ApiIntegration(
                id = id,
                name = organisationDocument.name,
                address = address,
                deal = deal,
                tags = tags,
                domain = organisationDocument.domain,
                allowsOverridingUserIds = organisationDocument.allowsOverridingUserIds ?: false,
                role = organisationDocument.role,
                features = features
            )

            OrganisationType.SCHOOL -> School(
                id = id,
                name = organisationDocument.name,
                address = address,
                deal = deal,
                tags = tags,
                domain = organisationDocument.domain,
                district = organisationDocument.parent?.let { fromDocument(it) as? District? },
                externalId = externalId,
                role = organisationDocument.role,
                features = features
            )

            OrganisationType.DISTRICT -> District(
                id = id,
                name = organisationDocument.name,
                address = address,
                deal = deal,
                tags = tags,
                domain = organisationDocument.domain,
                externalId = externalId,
                role = organisationDocument.role,
                features = features
            )

            OrganisationType.LTI_DEPLOYMENT -> LtiDeployment(
                id = id,
                name = organisationDocument.name,
                address = address,
                deal = deal,
                tags = tags,
                domain = organisationDocument.domain,
                deploymentId = organisationDocument.deploymentId!!,
                role = organisationDocument.role,
                features = features,
                parent = organisationDocument.parent!!.let { fromDocument(it) },
            )
        }
    }

    fun toDocument(organisation: Organisation): OrganisationDocument {
        val parent = when (organisation) {
            is School -> organisation.district
            is LtiDeployment -> organisation.parent
            else -> null
        }

        return OrganisationDocument(
            _id = ObjectId(organisation.id.value),
            name = organisation.name,
            domain = organisation.domain,
            role = organisation.role,
            externalId = when (organisation) {
                is School -> organisation.externalId?.value
                is District -> organisation.externalId?.value
                is ApiIntegration -> null
                is LtiDeployment -> null
            },
            deploymentId = when (organisation) {
                is LtiDeployment -> organisation.deploymentId
                else -> null
            },
            type = organisation.type(),
            country = organisation.address.country?.id?.let { LocationDocument(code = it) },
            state = organisation.address.state?.id?.let { LocationDocument(code = it) },
            postcode = organisation.address.postcode,
            allowsOverridingUserIds = (organisation as? ApiIntegration?)?.allowsOverridingUserIds,
            parent = parent?.let { toDocument(it) },
            accessExpiresOn = organisation.deal.accessExpiresOn?.toInstant(),
            tags = organisation.tags.map { it.name }.toSet(),
            billing = organisation.deal.billing,
            contentPackageId = organisation.deal.contentPackageId?.value,
            features = organisation.features?.mapKeys { FeatureDocumentConverter.toDocument(it.key) },
            prices = organisation.deal.prices?.let { prices ->
                mapOf(
                    VideoTypeKey.INSTRUCTIONAL to prices.instructional,
                    VideoTypeKey.NEWS to prices.news,
                    VideoTypeKey.STOCK to prices.stock
                )
            }
        )
    }
}
