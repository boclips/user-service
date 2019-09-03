package com.boclips.users.presentation.resources.school

import com.boclips.users.domain.model.school.Country
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import org.springframework.hateoas.Resource
import org.springframework.stereotype.Component

@Component
class CountryConverter(val organisationLinkBuilder: OrganisationLinkBuilder) {
    fun toCountriesResource(countries: List<Country>): List<Resource<CountryResource>> {
        return countries.map { country ->
            val statesLink = organisationLinkBuilder.getStatesLink(country)

            statesLink?.let {
                Resource(
                    CountryResource(id = country.id, name = country.name),
                    statesLink
                )
            } ?: Resource(CountryResource(id = country.id, name = country.name))
        }
    }
}