package com.boclips.users.presentation.resources

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.VideoTypePrices
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

        val instructionalVideoTypePrice = "123"
        val newsVideoTypePrice = "234"
        val stockVideoTypePrice = "345"
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
                    instructional = BigDecimal(instructionalVideoTypePrice),
                    news = BigDecimal(newsVideoTypePrice),
                    stock = BigDecimal(stockVideoTypePrice)
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
        assertThat(organisationResource.deal!!.prices!!.instructional).isEqualTo(instructionalVideoTypePrice)
        assertThat(organisationResource.deal!!.prices!!.news).isEqualTo(newsVideoTypePrice)
        assertThat(organisationResource.deal!!.prices!!.stock).isEqualTo(stockVideoTypePrice)
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
