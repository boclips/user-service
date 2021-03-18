package com.boclips.users.infrastructure.organisation

import com.boclips.security.utils.Client
import com.boclips.users.domain.model.access.ChannelId
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.access.VideoType
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.ContentAccess
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.LtiDeployment
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationTag
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.Prices
import com.boclips.users.domain.model.organisation.Prices.Price
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import mu.KLogging
import org.bson.types.ObjectId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Currency

object OrganisationDocumentConverter : KLogging() {
    fun fromDocument(organisationDocument: OrganisationDocument): Organisation {
        val id = OrganisationId(organisationDocument._id!!.toHexString())

        val address = Address(
            country = organisationDocument.country?.let { Country.fromCode(it.code) },
            state = organisationDocument.state?.let { State.fromCode(it.code) },
            postcode = organisationDocument.postcode
        )

        val deal = Deal(
            contentAccess = convertToDomainContentAccess(organisationDocument),
            billing = organisationDocument.billing ?: false,
            accessExpiresOn = organisationDocument.accessExpiresOn?.let { ZonedDateTime.ofInstant(it, ZoneOffset.UTC) },
            prices = organisationDocument.prices?.let {
                Prices(
                    videoTypePrices = it.videoTypePrices?.entries?.map { entry ->
                        when (entry.key) {
                            VideoTypeKey.INSTRUCTIONAL -> VideoType.INSTRUCTIONAL to convertToDomainPrice(entry.value)
                            VideoTypeKey.NEWS -> VideoType.NEWS to convertToDomainPrice(entry.value)
                            VideoTypeKey.STOCK -> VideoType.STOCK to convertToDomainPrice(entry.value)
                        }
                    }?.toMap() ?: emptyMap(),
                    channelPrices = it.channelPrices?.entries?.map { price ->
                        ChannelId(price.key) to convertToDomainPrice(price.value)
                    }?.toMap() ?: emptyMap(),
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
                features = features,
                legacyId = organisationDocument.legacyId
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
                features = features,
                legacyId = organisationDocument.legacyId
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
                features = features,
                legacyId = organisationDocument.legacyId
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
                legacyId = organisationDocument.legacyId
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
            contentPackageId = organisation.deal.contentAccess?.let { contentAccess ->
                when (contentAccess) {
                    is ContentAccess.SimpleAccess -> contentAccess.id.value
                    else -> null
                }
            },
            contentPackageByClient = organisation.deal.contentAccess?.let { convertToDocumentContentPackageByClient(it) },
            features = organisation.features?.mapKeys { FeatureDocumentConverter.toDocument(it.key) },
            prices = organisation.deal.prices?.let {
                CustomPricesDocument(
                    videoTypePrices = it.videoTypePrices
                        .map { entry ->
                            when (entry.key) {
                                VideoType.INSTRUCTIONAL -> VideoTypeKey.INSTRUCTIONAL to convertToPriceDocument(entry.value)
                                VideoType.NEWS -> VideoTypeKey.NEWS to convertToPriceDocument(entry.value)
                                VideoType.STOCK -> VideoTypeKey.STOCK to convertToPriceDocument(entry.value)
                            }
                        }.toMap(),
                    channelPrices = it.channelPrices.map { price -> price.key.value to convertToPriceDocument(price.value) }
                        .toMap()
                )
            },
            legacyId = organisation.legacyId
        )
    }

    private fun convertToDomainPrice(price: PriceDocument) = Price(
        amount = price.amount,
        currency = Currency.getInstance(price.currency)
    )

    private fun convertToPriceDocument(price: Price) = PriceDocument(
        amount = price.amount,
        currency = price.currency.currencyCode
    )

    private fun convertToDomainContentAccess(organisationDocument: OrganisationDocument): ContentAccess? {
        return when {
            organisationDocument.contentPackageId != null -> ContentAccess.SimpleAccess(
                ContentPackageId(
                    organisationDocument.contentPackageId
                )
            )
            organisationDocument.contentPackageByClient != null -> ContentAccess.ClientBasedAccess(
                organisationDocument.contentPackageByClient.entries.map {
                    Client.getClientByName(it.key) to ContentPackageId(it.value)
                }.toMap()
            )
            else -> null
        }
    }

    private fun convertToDocumentContentPackageByClient(contentAccess: ContentAccess): Map<String, String>? =
        if (contentAccess is ContentAccess.ClientBasedAccess) {
            contentAccess.clientAccess.entries.map {
                Client.getNameByClient(it.key).orEmpty() to it.value.value
            }.toMap()
        } else null
}
