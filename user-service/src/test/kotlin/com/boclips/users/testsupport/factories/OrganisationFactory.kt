package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.access.ChannelId
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.access.VideoType
import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.LtiDeployment
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationTag
import com.boclips.users.domain.model.organisation.Prices
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.Currency

class OrganisationFactory {
    companion object {

        fun address(
            country: Country? = null
        ): Address = Address(
            country = country
        )

        fun deal(
            contentPackageId: ContentPackageId? = null,
            billing: Boolean = false,
            accessExpiresOn: ZonedDateTime? = null,
            prices: Prices? = null
        ): Deal = Deal(
            contentPackageId = contentPackageId,
            billing = billing,
            accessExpiresOn = accessExpiresOn,
            prices = prices
        )

        fun pricedDeal(
            contentPackageId: ContentPackageId? = null,
            billing: Boolean = false,
            accessExpiresOn: ZonedDateTime? = null,
        ): Deal = Deal(
            contentPackageId = contentPackageId,
            billing = billing,
            accessExpiresOn = accessExpiresOn,
            prices = Prices(
                videoTypePrices = mapOf(
                    VideoType.INSTRUCTIONAL to Prices.Price(BigDecimal.valueOf(10), Currency.getInstance("GBP")),
                    VideoType.NEWS to Prices.Price(BigDecimal.valueOf(4), Currency.getInstance("GBP")),
                    VideoType.STOCK to Prices.Price(BigDecimal.valueOf(5), Currency.getInstance("GBP"))
                ),
                channelPrices = mapOf(
                    ChannelId("channel-TED") to PriceFactory.sample(amount = BigDecimal.ONE),
                    ChannelId("channel-orange") to PriceFactory.sample(amount = BigDecimal.TEN)
                )
             )
        )

        fun district(
            id: OrganisationId = OrganisationId(),
            name: String = "A District",
            address: Address = address(country = Country.usa()),
            deal: Deal = deal(),
            role: String? = null,
            domain: String? = null,
            tags: Set<OrganisationTag> = emptySet(),
            externalId: ExternalOrganisationId? = null,
            features: Map<Feature, Boolean>? = null,
            legacyId: String? = null
        ): District {
            return District(
                id = id,
                name = name,
                address = address,
                deal = deal,
                tags = tags,
                role = role,
                externalId = externalId,
                domain = domain,
                features = features,
                legacyId = legacyId
            )
        }

        fun ltiDeployment(
            id: OrganisationId = OrganisationId(),
            name: String = "lti deployment",
            address: Address = address(country = Country.usa()),
            deal: Deal = deal(),
            role: String? = null,
            domain: String? = null,
            tags: Set<OrganisationTag> = emptySet(),
            deploymentId: String = "deployment-id",
            features: Map<Feature, Boolean>? = null,
            parent: Organisation = apiIntegration()
        ): LtiDeployment {
            return LtiDeployment(
                id = id,
                name = name,
                address = address,
                deal = deal,
                tags = tags,
                role = role,
                deploymentId = deploymentId,
                domain = domain,
                features = features,
                parent = parent
            )
        }

        fun school(
            id: OrganisationId = OrganisationId(),
            name: String = "A School",
            address: Address = address(),
            deal: Deal = deal(),
            role: String? = null,
            tags: Set<OrganisationTag> = emptySet(),
            domain: String? = null,
            district: District? = null,
            externalId: ExternalOrganisationId? = null,
            features: Map<Feature, Boolean>? = null,
            legacyId: String? = null
        ): School {
            return School(
                id = id,
                name = name,
                address = address,
                deal = deal,
                tags = tags,
                role = role,
                externalId = externalId,
                domain = domain,
                district = district,
                features = features,
                legacyId = legacyId
            )
        }

        fun apiIntegration(
            id: OrganisationId = OrganisationId(),
            name: String = "An API Customer",
            address: Address = address(),
            deal: Deal = deal(),
            tags: Set<OrganisationTag> = emptySet(),
            role: String? = null,
            domain: String? = null,
            allowsOverridingUserId: Boolean = false,
            features: Map<Feature, Boolean>? = null
        ): ApiIntegration {
            return ApiIntegration(
                id = id,
                name = name,
                address = address,
                deal = deal,
                tags = tags,
                role = role,
                domain = domain,
                allowsOverridingUserIds = allowsOverridingUserId,
                features = features
            )
        }
    }
}
