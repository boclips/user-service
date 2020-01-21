package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.GetCountries
import com.boclips.users.application.commands.GetUsStates
import com.boclips.users.presentation.hateoas.CountryLinkBuilder
import com.boclips.users.presentation.resources.school.CountryConverter
import com.boclips.users.presentation.resources.school.CountryResource
import com.boclips.users.presentation.resources.school.StateConverter
import com.boclips.users.presentation.resources.school.StateResource
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
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
    fun getAllCountries(): CollectionModel<EntityModel<CountryResource>> {
        val countries = getCountries()

        return CollectionModel(
            countryConverter.toCountriesResource(countries), countryLinkBuilder.getCountriesSelfLink()
        )
    }

    @GetMapping("/countries/USA/states")
    fun getAllUsStates(): CollectionModel<StateResource> {
        val states = getUsStates()

        return CollectionModel(
            stateConverter.toStatesResource(states),
            countryLinkBuilder.getUsStatesSelfLink()
        )
    }
}
