package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.GetCountries
import com.boclips.users.application.commands.GetUsStates
import com.boclips.users.application.commands.SearchSchools
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.presentation.resources.school.CountryConverter
import com.boclips.users.presentation.resources.school.CountryResource
import com.boclips.users.presentation.resources.school.SchoolResource
import com.boclips.users.presentation.resources.school.StateResource
import org.springframework.hateoas.Resource
import org.springframework.hateoas.Resources
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class OrganisationController(
    private val getCountries: GetCountries,
    private val getUsStates: GetUsStates,
    private val countryConverter: CountryConverter,
    private val organisationLinkBuilder: OrganisationLinkBuilder,
    private val searchSchools: SearchSchools
) {

    @GetMapping("/countries")
    fun getAllCountries(): Resources<Resource<CountryResource>> {
        val countries = getCountries()

        return Resources(
            countryConverter.toCountriesResource(countries), organisationLinkBuilder.getCountriesSelfLink()
        )
    }

    @GetMapping("/countries/USA/states")
    fun getAllUsStates(): Resources<StateResource> {
        val states = getUsStates()

        return Resources(
            states.map { StateResource(id = it.id, name = it.name) },
            organisationLinkBuilder.getUsStatesSelfLink()
        )
    }

    @GetMapping("/schools")
    fun getSchools(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) state: String?,
        @RequestParam(required = true) country: String?
    ): Resources<SchoolResource> {
        val schools = searchSchools(school = query, state = state, countryId = country)

        return Resources(
            schools.map { SchoolResource(id = it.id.value, name = it.name) })
    }
}