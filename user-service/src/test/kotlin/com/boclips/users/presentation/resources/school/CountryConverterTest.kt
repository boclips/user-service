package com.boclips.users.presentation.resources.school

import com.boclips.users.api.response.country.CountryResource
import com.boclips.users.api.response.state.StatesResource
import com.boclips.users.api.response.state.StatesWrapperResource
import com.boclips.users.domain.model.school.Country
import com.boclips.users.presentation.controllers.CountryConverter
import com.boclips.users.presentation.converters.StateConverter
import com.boclips.users.presentation.hateoas.CountryLinkBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.hateoas.Link

class CountryConverterTest {
    @Test
    fun `convert list of countries to list of EntityModel of CountryResource`() {
        val countryLinkBuilder: CountryLinkBuilder = mock {
            on { getStatesLink(any()) } doReturn Link("link")
        }
        val stateConverter = mock<StateConverter> {
            on { toStatesResource(any()) } doReturn StatesResource(
                _embedded = StatesWrapperResource(
                    emptyList()
                ),
                _links = null
            )
        }

        val countries: List<Country> = listOf(
            Country(id = "USA", name = "United States")
        )

        val countryResources =
            CountryConverter(
                mock(),
                countryLinkBuilder,
                stateConverter
            ).toCountriesResource(countries)

        assertThat(countryResources._embedded.countries).isNotNull
        assertThat(countryResources._embedded.countries).hasSize(1)
        assertThat(countryResources._embedded.countries[0]).isEqualTo(
            CountryResource(
                id = "USA",
                name = "United States",
                states = emptyList(),
                _links = mapOf("self" to Link("link"))
            )
        )
    }
}
