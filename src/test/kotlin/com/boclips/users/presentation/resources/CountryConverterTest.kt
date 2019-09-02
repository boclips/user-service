package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.school.Country
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CountryConverterTest {

    @Test
    fun `convert list of countries to list of CountryResource`() {
        val countries: List<Country> = listOf(
            Country(id = "AD", name = "Andorra"),
            Country(id = "HU", name = "Hungary")
        )

        val countryResources = CountryConverter().toCountriesResource(countries)

        assertThat(countryResources).isNotNull
        assertThat(countryResources).hasSize(2)
        assertThat(countryResources[0]).isEqualTo(CountryResource(id = "AD", name = "Andorra"))
        assertThat(countryResources[1]).isEqualTo(CountryResource(id = "HU", name = "Hungary"))
    }
}