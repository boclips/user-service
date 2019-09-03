package com.boclips.users.presentation.resources.school

import com.boclips.users.domain.model.school.Country
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
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
            on { getUsStatesLink() } doReturn Link("link")
        }

        val countries: List<Country> = listOf(
            Country(id = "USA", name = "United States"),
            Country(id = "HUN", name = "Hungary")
        )

        val countryResources = CountryConverter(organisationLinkBuilder).toCountriesResource(countries)

        assertThat(countryResources).isNotNull
        assertThat(countryResources).hasSize(2)
        assertThat(countryResources[0]).isEqualTo(
            Resource(
                CountryResource(
                    id = "USA",
                    name = "United States"
                ), Link("link")
            )
        )
        assertThat(countryResources[1]).isEqualTo(
            Resource(
                CountryResource(
                    id = "HUN",
                    name = "Hungary"
                )
            )
        )
    }
}