package com.boclips.users.api.httpclient.test.fakes

import com.boclips.users.api.factories.OrganisationResourceFactory
import com.boclips.users.api.request.OrganisationFilterRequest
import com.boclips.users.api.response.organisation.DealResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrganisationsClientFakeTest {

    @Test
    fun `can get organisations with custom pricing`() {
        val fake = OrganisationsClientFake()
        fake.add(
            OrganisationResourceFactory.sample(
                id = "with-price",
                deal = OrganisationResourceFactory.sampleDeal(
                    prices = DealResource.PricesResource(
                        videoTypePrices = mapOf(
                            "STOCK" to DealResource.PriceResource(
                                "10",
                                "USD"
                            )
                        )
                    )
                )
            )
        )
        fake.add(OrganisationResourceFactory.sample(id = "no-price"))

        val organisations =
            fake.getOrganisations(OrganisationFilterRequest().apply { hasCustomPrices = true })._embedded.organisations
        assertThat(organisations).hasSize(1)
        assertThat(organisations.first().id).isEqualTo("with-price")
    }

    @Test
    fun `can get all organisations without custom pricing` () {
        val fake = OrganisationsClientFake()
        fake.add(
            OrganisationResourceFactory.sample(
                id = "with-price",
                deal = OrganisationResourceFactory.sampleDeal(
                    prices = DealResource.PricesResource(
                        videoTypePrices = mapOf(
                            "STOCK" to DealResource.PriceResource(
                                "10",
                                "USD"
                            )
                        )
                    )
                )
            )
        )
        fake.add(OrganisationResourceFactory.sample(id = "no-price"))

        val organisations =
            fake.getOrganisations(OrganisationFilterRequest().apply { hasCustomPrices = false })._embedded.organisations
        assertThat(organisations).hasSize(1)
        assertThat(organisations.first().id).isEqualTo("no-price")
    }

    @Test
    fun `can get all organisations`() {
        val fake = OrganisationsClientFake()
        fake.add(
            OrganisationResourceFactory.sample(
                id = "with-price",
                deal = OrganisationResourceFactory.sampleDeal(
                    prices = DealResource.PricesResource(
                        videoTypePrices = mapOf(
                            "STOCK" to DealResource.PriceResource(
                                "10",
                                "USD"
                            )
                        )
                    )
                )
            )
        )
        fake.add(OrganisationResourceFactory.sample(id = "no-price"))

        val organisations =
            fake.getOrganisations(OrganisationFilterRequest())._embedded.organisations
        assertThat(organisations).hasSize(2)
    }
}
