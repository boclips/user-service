package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationTag
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import java.time.ZonedDateTime

class OrganisationFactory {
    companion object {

        fun address(
            country: Country? = null
        ): Address = Address(
            country = country
        )

        fun deal(
            contentPackageId: ContentPackageId? = null,
            accessExpiresOn: ZonedDateTime? = null
        ): Deal = Deal(
            contentPackageId = contentPackageId,
            accessExpiresOn = accessExpiresOn
        )

        fun district(
            id: OrganisationId = OrganisationId(),
            name: String = "A District",
            address: Address = address(country = Country.usa()),
            deal: Deal = deal(),
            role: String? = null,
            domain: String? = null,
            tags: Set<OrganisationTag> = emptySet(),
            externalId: ExternalOrganisationId? = null
        ): District {
            return District(
                id = id,
                name = name,
                address = address,
                deal = deal,
                tags = tags,
                role = role,
                externalId = externalId,
                domain = domain
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
            externalId: ExternalOrganisationId? = null
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
                district = district
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
            allowsOverridingUserId: Boolean = false
        ): ApiIntegration {
            return ApiIntegration(
                id = id,
                name = name,
                address = address,
                deal = deal,
                tags = tags,
                role = role,
                domain = domain,
                allowsOverridingUserIds = allowsOverridingUserId
            )
        }
    }
}
