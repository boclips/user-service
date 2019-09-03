package com.boclips.users.presentation.resources.school

import com.boclips.users.domain.model.school.Country
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.hateoas.Link
import org.springframework.hateoas.Resource

class CountryConverterTest {

    @Test
    fun `convert list of countries to list of Resource of CountryResource`() {

        val organisationLinkBuilder: OrganisationLinkBuilder = mock {
            on { getStatesLink(any<Country>()) } doReturn Link("link")
        }

        val countries: List<Country> = listOf(
            Country(id = "USA", name = "United States")
        )

        val countryResources = CountryConverter(organisationLinkBuilder).toCountriesResource(countries)

        assertThat(countryResources).isNotNull
        assertThat(countryResources).hasSize(1)
        assertThat(countryResources[0]).isEqualTo(
            Resource(CountryResource(
                id = "USA",
                name = "United States"
            ), Link("link"))
        )
    }
}