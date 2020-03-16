package com.boclips.users.presentation.resources.school

import com.boclips.users.domain.model.school.Country
import com.boclips.users.presentation.hateoas.CountryLinkBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link

class CountryConverterTest {
    @Test
    fun `convert list of countries to list of EntityModel of CountryResource`() {
        val countryLinkBuilder: CountryLinkBuilder = mock {
            on { getStatesLink(any()) } doReturn Link("link")
        }
        val stateConverter = mock<StateConverter> { on { toStatesResource(any()) } doReturn emptyList() }

        val countries: List<Country> = listOf(
            Country(id = "USA", name = "United States")
        )

        val countryResources =
            CountryConverter(mock(), countryLinkBuilder, stateConverter).toCountriesResource(countries)

        assertThat(countryResources).isNotNull
        assertThat(countryResources).hasSize(1)
        assertThat(countryResources[0]).isEqualTo(
            EntityModel(
                CountryResource(
                    id = "USA",
                    name = "United States",
                    states = emptyList()
                ), Link("link")
            )
        )
    }
}
