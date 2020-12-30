package com.boclips.users.presentation.resources

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.VideoTypePrices
import com.boclips.users.domain.model.organisation.VideoTypePrices.Price
import com.boclips.users.domain.model.school.State
import com.boclips.users.presentation.converters.OrganisationConverter
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

class OrganisationConverterTest : AbstractSpringIntegrationTest() {
    lateinit var organisationLinkBuilder: OrganisationLinkBuilder
    lateinit var organisationConverter: OrganisationConverter

    @BeforeEach
    fun setUp() {
        organisationLinkBuilder = OrganisationLinkBuilder(mock())

        organisationConverter =
            OrganisationConverter(
                organisationLinkBuilder
            )
    }

    @Test
    fun toResource() {
        setSecurityContext("org-viewer", UserRoles.UPDATE_ORGANISATIONS)

        val instructionalVideoTypePrice = Price(BigDecimal("123"), Price.DEFAULT_CURRENCY)
        val newsVideoTypePrice = Price(BigDecimal("234"), Price.DEFAULT_CURRENCY)
        val stockVideoTypePrice = Price(BigDecimal("345"), Price.DEFAULT_CURRENCY)
        val originalOrganisation = OrganisationFactory.district(
            id = OrganisationId("organisation-account-id"),
            name = "my-district",
            address = Address(
                state = State.fromCode("NY")
            ),
            deal = Deal(
                contentPackageId = ContentPackageId("content-package-id"),
                billing = false,
                accessExpiresOn = ZonedDateTime.parse("2019-12-04T15:11:59.531Z"),
                prices = VideoTypePrices(
                    instructional = instructionalVideoTypePrice,
                    news = newsVideoTypePrice,
                    stock = stockVideoTypePrice
                )
            ),
            features = mapOf(Feature.USER_DATA_HIDDEN to true)
        )

        val organisationResource = organisationConverter.toResource(originalOrganisation)

        assertThat(organisationResource.id).isEqualTo(originalOrganisation.id.value)
        assertThat(organisationResource.accessExpiresOn).isEqualTo(originalOrganisation.deal.accessExpiresOn)
        assertThat(organisationResource.deal!!.accessExpiresOn).isEqualTo(originalOrganisation.deal.accessExpiresOn)
        assertThat(organisationResource.contentPackageId).isEqualTo(originalOrganisation.deal.contentPackageId!!.value)
        assertThat(organisationResource.deal!!.contentPackageId).isEqualTo(originalOrganisation.deal.contentPackageId!!.value)
        assertThat(organisationResource.billing).isEqualTo(originalOrganisation.deal.billing)
        assertThat(organisationResource.deal!!.billing).isEqualTo(originalOrganisation.deal.billing)
        assertThat(organisationResource.deal!!.prices!!.instructional!!.amount).isEqualTo("123")
        assertThat(organisationResource.deal!!.prices!!.instructional!!.currency).isEqualTo("USD")
        assertThat(organisationResource.deal!!.prices!!.news!!.amount).isEqualTo("234")
        assertThat(organisationResource.deal!!.prices!!.news!!.currency).isEqualTo("USD")
        assertThat(organisationResource.deal!!.prices!!.stock!!.amount).isEqualTo("345")
        assertThat(organisationResource.deal!!.prices!!.stock!!.currency).isEqualTo("USD")
        assertThat(organisationResource.organisationDetails.name).isEqualTo(originalOrganisation.name)
        assertThat(organisationResource.organisationDetails.country?.name).isEqualTo(originalOrganisation.address.country?.name)
        assertThat(organisationResource.organisationDetails.state?.name).isEqualTo(originalOrganisation.address.state?.name)
        assertThat(organisationResource.organisationDetails.type).isEqualTo(
            originalOrganisation.type().toString()
        )
        assertThat(organisationResource.organisationDetails.features!![Feature.USER_DATA_HIDDEN.name]).isTrue()
        assertThat(organisationResource._links?.map { it.key }).containsExactlyInAnyOrder(
            "self",
            "edit",
            "associateUsers"
        )
    }
}
