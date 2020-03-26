package com.boclips.users.presentation.controllers

import com.boclips.users.api.response.country.CountriesResource
import com.boclips.users.api.response.country.CountriesWrapperResource
import com.boclips.users.api.response.country.CountryResource
import com.boclips.users.domain.model.school.Country
import com.boclips.users.presentation.converters.StateConverter
import com.boclips.users.presentation.hateoas.CountryLinkBuilder
import com.boclips.users.presentation.hateoas.SchoolLinkBuilder
import org.springframework.stereotype.Component

@Component
class CountryConverter(
    private val schoolLinkBuilder: SchoolLinkBuilder,
    private val countryLinkBuilder: CountryLinkBuilder,
    private val stateConverter: StateConverter
) {
    fun toCountriesResource(countries: List<Country>): CountriesResource {
        return CountriesResource(
            _embedded = CountriesWrapperResource(countries = countries.map { toCountryResource(it) }),
            _links = listOfNotNull(
                countryLinkBuilder.getCountriesSelfLink()
            ).map { it.rel.value() to it }.toMap()
        )
    }

    fun toCountryResource(country: Country) = CountryResource(
        id = country.id,
        name = country.name,
        states = stateConverter.toStateResources(country.states),
        _links = listOfNotNull(
            countryLinkBuilder.getStatesLink(country),
            schoolLinkBuilder.getSchoolLink(country.id)
        ).map { it.rel.value() to it }.toMap()
    )
}
