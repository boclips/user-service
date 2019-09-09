package com.boclips.users.presentation.resources.school

import com.boclips.users.domain.model.school.Country
import com.boclips.users.presentation.hateoas.CountryLinkBuilder
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import org.springframework.hateoas.Resource
import org.springframework.stereotype.Component

@Component
class CountryConverter(
    private val organisationLinkBuilder: OrganisationLinkBuilder,
    private val countryLinkBuilder: CountryLinkBuilder
) {
    fun toCountriesResource(countries: List<Country>): List<Resource<CountryResource>> {
        return countries.map {
            Resource(
                CountryResource(id = it.id, name = it.name),
                listOfNotNull(
                    countryLinkBuilder.getStatesLink(it),
                    organisationLinkBuilder.getSchoolLink(it.id)
                )
            )
        }
    }
}