package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.school.Country
import org.springframework.stereotype.Component

@Component
class CountryConverter {
    fun toCountriesResource(countries: List<Country>): List<CountryResource> {
        return countries.map { CountryResource(id = it.id, name = it.name) }
    }
}