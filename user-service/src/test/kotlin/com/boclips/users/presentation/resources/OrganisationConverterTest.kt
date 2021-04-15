package com.boclips.users.presentation.resources

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.api.response.feature.FeatureKeyResource
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.access.ChannelId
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.access.VideoType
import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ContentAccess
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.Prices
import com.boclips.users.domain.model.school.State
import com.boclips.users.presentation.converters.OrganisationConverter
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.PriceFactory
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

        val instructionalVideoTypePrice = PriceFactory.sample(BigDecimal("123"))
        val newsVideoTypePrice = PriceFactory.sample(BigDecimal("234"))
        val stockVideoTypePrice = PriceFactory.sample(BigDecimal("345"))
        val originalOrganisation = OrganisationFactory.district(
            id = OrganisationId("organisation-account-id"),
            name = "my-district",
            address = Address(
                state = State.fromCode("NY")
            ),
            deal = Deal(
                contentAccess = ContentAccess.SimpleAccess(ContentPackageId("content-package-id")),
                billing = false,
                accessExpiresOn = ZonedDateTime.parse("2019-12-04T15:11:59.531Z"),
                prices = Prices(
                    videoTypePrices = mapOf(
                        VideoType.INSTRUCTIONAL to instructionalVideoTypePrice,
                        VideoType.NEWS to newsVideoTypePrice,
                        VideoType.STOCK to stockVideoTypePrice,
                    ),
                    channelPrices = mapOf(
                        ChannelId("channel-TED") to PriceFactory.sample(amount = BigDecimal.ONE),
                        ChannelId("channel-orange") to PriceFactory.sample(amount = BigDecimal.TEN),
                        ChannelId("channel-GME") to PriceFactory.sample(amount = BigDecimal.valueOf(1000))
                    )
                )
            ),
            features = mapOf(Feature.USER_DATA_HIDDEN to true)
        )

        val organisationResource = organisationConverter.toResource(originalOrganisation)

        assertThat(organisationResource.id).isEqualTo(originalOrganisation.id.value)
        assertThat(organisationResource.accessExpiresOn).isEqualTo(originalOrganisation.deal.accessExpiresOn)
        assertThat(organisationResource.deal.accessExpiresOn).isEqualTo(originalOrganisation.deal.accessExpiresOn)
        assertThat(organisationResource.contentPackageId).isEqualTo((originalOrganisation.deal.contentAccess as? ContentAccess.SimpleAccess)?.id!!.value)
        assertThat(organisationResource.deal.contentPackageId).isEqualTo((originalOrganisation.deal.contentAccess as? ContentAccess.SimpleAccess)?.id!!.value)
        assertThat(organisationResource.billing).isEqualTo(originalOrganisation.deal.billing)
        assertThat(organisationResource.deal.billing).isEqualTo(originalOrganisation.deal.billing)
        assertThat(organisationResource.deal.prices!!.videoTypePrices["INSTRUCTIONAL"]!!.amount).isEqualTo("123")
        assertThat(organisationResource.deal.prices!!.videoTypePrices["INSTRUCTIONAL"]!!.currency).isEqualTo("USD")
        assertThat(organisationResource.deal.prices!!.videoTypePrices["NEWS"]!!.amount).isEqualTo("234")
        assertThat(organisationResource.deal.prices!!.videoTypePrices["NEWS"]!!.currency).isEqualTo("USD")
        assertThat(organisationResource.deal.prices!!.videoTypePrices["STOCK"]!!.amount).isEqualTo("345")
        assertThat(organisationResource.deal.prices!!.videoTypePrices["STOCK"]!!.currency).isEqualTo("USD")
        assertThat(organisationResource.deal.prices!!.channelPrices["channel-TED"]!!.amount).isEqualTo("1")
        assertThat(organisationResource.deal.prices!!.channelPrices["channel-orange"]!!.amount).isEqualTo("10")
        assertThat(organisationResource.deal.prices!!.channelPrices["channel-GME"]!!.amount).isEqualTo("1000")
        assertThat(organisationResource.organisationDetails.name).isEqualTo(originalOrganisation.name)
        assertThat(organisationResource.organisationDetails.country?.name).isEqualTo(originalOrganisation.address.country?.name)
        assertThat(organisationResource.organisationDetails.state?.name).isEqualTo(originalOrganisation.address.state?.name)
        assertThat(organisationResource.organisationDetails.type).isEqualTo(
            originalOrganisation.type().toString()
        )
        assertThat(organisationResource.organisationDetails.features!![FeatureKeyResource.USER_DATA_HIDDEN]).isTrue()
        assertThat(organisationResource._links?.map { it.key }).containsExactlyInAnyOrder(
            "self",
            "edit",
            "associateUsers"
        )
    }
}
