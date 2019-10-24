package com.boclips.users.presentation.resources.school

import com.boclips.users.domain.model.school.Country
import com.boclips.users.presentation.hateoas.CountryLinkBuilder
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import org.springframework.hateoas.EntityModel
import org.springframework.stereotype.Component

@Component
class CountryConverter(
    private val organisationLinkBuilder: OrganisationLinkBuilder,
    private val countryLinkBuilder: CountryLinkBuilder,
    private val stateConverter: StateConverter
) {
    fun toCountriesResource(countries: List<Country>): List<EntityModel<CountryResource>> {
        return countries.map {
            EntityModel(
                CountryResource(id = it.id, name = it.name, states = stateConverter.toStatesResource(it.states)),
                listOfNotNull(
                    countryLinkBuilder.getStatesLink(it),
                    organisationLinkBuilder.getSchoolLink(it.id)
                )
            )
        }
    }
}
