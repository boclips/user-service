package com.boclips.users.presentation.converters

import com.boclips.users.api.response.organisation.DealResource
import com.boclips.users.api.response.organisation.DealResource.VideoTypePricesResource
import com.boclips.users.api.response.organisation.OrganisationResource
import com.boclips.users.api.response.organisation.OrganisationsResource
import com.boclips.users.api.response.organisation.OrganisationsWrapper
import com.boclips.users.domain.model.Page
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.VideoTypePrices
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import org.springframework.hateoas.PagedModel
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.text.DecimalFormat

@Component
class OrganisationConverter(
    private val organisationLinkBuilder: OrganisationLinkBuilder
) {

    fun toResource(organisation: Organisation): OrganisationResource {
        return OrganisationResource(
            id = organisation.id.value,
            contentPackageId = organisation.deal.contentPackageId?.value,
            accessExpiresOn = organisation.deal.accessExpiresOn,
            billing = organisation.deal.billing,
            deal = DealResource(
                contentPackageId = organisation.deal.contentPackageId?.value,
                accessExpiresOn = organisation.deal.accessExpiresOn,
                billing = organisation.deal.billing,
                prices = organisation.deal.prices?.toResource()
            ),
            organisationDetails = OrganisationDetailsConverter().toResource(organisation),
            _links = listOfNotNull(
                organisationLinkBuilder.self(organisation.id),
                organisationLinkBuilder.editOrganisation(organisation.id),
                organisationLinkBuilder.associateUsersToOrganisation(organisation.id)
            ).map { it.rel.value() to it }.toMap()
        )
    }

    fun toResource(organisations: Page<Organisation>): OrganisationsResource {
        return OrganisationsResource(
            _embedded = OrganisationsWrapper(
                organisations = organisations.items.map {
                    toResource(it)
                }),
            page = PagedModel.PageMetadata(
                organisations.pageSize.toLong(),
                organisations.pageNumber.toLong(),
                organisations.totalElements
            ),
            _links = null
        )
    }

    private fun VideoTypePrices.toResource(): VideoTypePricesResource {
        return VideoTypePricesResource(
            instructional = convertToString(instructional),
            news = convertToString(news),
            stock = convertToString(stock),
        )
    }

    companion object {

        private val format: DecimalFormat = DecimalFormat().also { it.maximumFractionDigits = 2 }

        private fun convertToString(price: BigDecimal?): String? = price?.let { format.format(it) }
    }
}
