package com.boclips.users.presentation.resources.school

import com.boclips.users.domain.model.school.Country
import com.boclips.users.presentation.hateoas.CountryLinkBuilder
import com.boclips.users.presentation.hateoas.AccountLinkBuilder
import org.springframework.hateoas.Resource
import org.springframework.stereotype.Component

@Component
class CountryConverter(
    private val accountLinkBuilder: AccountLinkBuilder,
    private val countryLinkBuilder: CountryLinkBuilder,
    private val stateConverter: StateConverter
) {
    fun toCountriesResource(countries: List<Country>): List<Resource<CountryResource>> {
        return countries.map {
            Resource(
                CountryResource(id = it.id, name = it.name, states = stateConverter.toStatesResource(it.states)),
                listOfNotNull(
                    countryLinkBuilder.getStatesLink(it),
                    accountLinkBuilder.getSchoolLink(it.id)
                )
            )
        }
    }
}
