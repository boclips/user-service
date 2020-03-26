package com.boclips.users.presentation.controllers

import com.boclips.users.api.response.country.CountriesResource
import com.boclips.users.api.response.state.StatesResource
import com.boclips.users.application.commands.GetCountries
import com.boclips.users.application.commands.GetUsStates
import com.boclips.users.presentation.hateoas.CountryLinkBuilder
import com.boclips.users.presentation.converters.StateConverter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class CountryController(
    private val getCountries: GetCountries,
    private val getUsStates: GetUsStates,
    private val countryConverter: CountryConverter,
    private val stateConverter: StateConverter,
    private val countryLinkBuilder: CountryLinkBuilder
) {
    @GetMapping("/countries")
    fun getAllCountries(): CountriesResource {
        val countries = getCountries()

        return countryConverter.toCountriesResource(countries)
    }

    @GetMapping("/countries/USA/states")
    fun getAllUsStates(): StatesResource {
        val states = getUsStates()

        return   stateConverter.toStatesResource(states)
    }
}
