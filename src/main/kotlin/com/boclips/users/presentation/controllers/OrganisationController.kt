package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.CreateApiIntegration
import com.boclips.users.application.commands.GetCountries
import com.boclips.users.application.commands.GetOrganisationById
import com.boclips.users.application.commands.GetApiIntegrationByName
import com.boclips.users.application.commands.GetUsStates
import com.boclips.users.application.commands.SearchSchools
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.presentation.requests.CreateOrganisationRequest
import com.boclips.users.presentation.resources.OrganisationConverter
import com.boclips.users.presentation.resources.OrganisationResource
import com.boclips.users.presentation.resources.school.CountryConverter
import com.boclips.users.presentation.resources.school.CountryResource
import com.boclips.users.presentation.resources.school.SchoolResource
import com.boclips.users.presentation.resources.school.StateResource
import org.springframework.hateoas.Resource
import org.springframework.hateoas.Resources
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
@RestController
@RequestMapping("/v1", "/v1/")
class OrganisationController(
    private val getCountries: GetCountries,
    private val getUsStates: GetUsStates,
    private val countryConverter: CountryConverter,
    private val organisationLinkBuilder: OrganisationLinkBuilder,
    private val createApiIntegration: CreateApiIntegration,
    private val getOrganisationById: GetOrganisationById,
    private val getApiIntegrationByName: GetApiIntegrationByName,
    private val organisationConverter: OrganisationConverter,
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

    @PostMapping("/api-integrations")
    fun insertApiIntegration(@Valid @RequestBody request: CreateOrganisationRequest): ResponseEntity<Resource<*>> {
        val createdOrganisation = createApiIntegration(request)

        val headers = HttpHeaders()
        headers.set(HttpHeaders.LOCATION, organisationLinkBuilder.self(createdOrganisation.id).href)

        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @GetMapping("/organisations/{id}")
    fun fetchOrganisationById(@PathVariable("id") id: String): Resource<OrganisationResource> {
        val organisation = getOrganisationById(id)

        return Resource(
            organisationConverter.toResource(organisation),
            listOfNotNull(
                organisationLinkBuilder.self(organisation.id)
            )
        )
    }

    @GetMapping("/api-integrations")
    fun fetchApiIntegrationByName(@NotBlank @RequestParam(required = false) name: String?): Resource<OrganisationResource> {
        val apiIntegration = getApiIntegrationByName(name!!)

        return Resource(
            organisationConverter.toResource(apiIntegration),
            listOfNotNull(
                organisationLinkBuilder.self(apiIntegration.id)
            )
        )
    }

    @GetMapping("/schools")
    fun searchSchools(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) state: String?,
        @RequestParam(required = true) countryCode: String?
    ): Resources<SchoolResource> {
        val schools = searchSchools(schoolName = query, state = state, countryCode = countryCode)

        return Resources(
            schools.map { SchoolResource(id = it.id, name = it.name) })
    }
}