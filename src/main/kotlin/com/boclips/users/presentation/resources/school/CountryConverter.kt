package com.boclips.users.presentation.resources.school

import com.boclips.users.domain.model.school.Country
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import org.springframework.hateoas.Resource
import org.springframework.stereotype.Component

@Component
class CountryConverter(val organisationLinkBuilder: OrganisationLinkBuilder) {
    fun toCountriesResource(countries: List<Country>): List<Resource<CountryResource>> {
        return countries.map {
            when {
                it.isUSA() -> Resource(CountryResource(id = it.id, name = it.name), organisationLinkBuilder.getStatesLink(it))
                else -> Resource(CountryResource(id = it.id, name = it.name))
            }
        }
    }
}