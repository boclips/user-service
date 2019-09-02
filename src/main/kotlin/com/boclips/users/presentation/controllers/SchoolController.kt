package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.GetCountries
import com.boclips.users.presentation.hateoas.SchoolLinkBuilder
import com.boclips.users.presentation.hateoas.UserLinkBuilder
import com.boclips.users.presentation.resources.CountryConverter
import com.boclips.users.presentation.resources.CountryResource
import org.springframework.hateoas.LinkBuilder
import org.springframework.hateoas.Resources
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class SchoolController(
    private val getCountries: GetCountries,
    private val countryConverter: CountryConverter,
    private val uriLinkBuilder: SchoolLinkBuilder
) {

    @GetMapping("/countries")
    fun getAllCountries(): Resources<CountryResource> {
        val countries = getCountries()

        return Resources(countryConverter.toCountriesResource(countries), uriLinkBuilder.getCountriesSelfLink())
    }
}