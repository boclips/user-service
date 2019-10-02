package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.CreateApiIntegration
import com.boclips.users.application.commands.GetApiIntegrationByName
import com.boclips.users.application.commands.GetOrganisationById
import com.boclips.users.presentation.annotations.BoclipsE2ETestSupport
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.presentation.requests.CreateOrganisationRequest
import com.boclips.users.presentation.resources.OrganisationConverter
import com.boclips.users.presentation.resources.OrganisationAccountResource
import org.springframework.hateoas.Resource
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

@BoclipsE2ETestSupport
@Validated
@RestController
@RequestMapping("/v1", "/v1/")
class OrganisationTestSupportController(
    private val organisationLinkBuilder: OrganisationLinkBuilder,
    private val createApiIntegration: CreateApiIntegration,
    private val getOrganisationById: GetOrganisationById,
    private val getApiIntegrationByName: GetApiIntegrationByName,
    private val organisationConverter: OrganisationConverter
) {
    @PostMapping("/api-integrations")
    fun insertApiIntegration(@Valid @RequestBody request: CreateOrganisationRequest): ResponseEntity<Resource<*>> {
        val createdOrganisation = createApiIntegration(request)

        val headers = HttpHeaders()
        headers.set(HttpHeaders.LOCATION, organisationLinkBuilder.self(createdOrganisation.id).href)

        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @GetMapping("/organisations/{id}")
    fun fetchOrganisationById(@PathVariable("id") id: String): Resource<OrganisationAccountResource> {
        val organisation = getOrganisationById(id)

        return Resource(
            organisationConverter.toResource(organisation),
            listOfNotNull(
                organisationLinkBuilder.self(organisation.id)
            )
        )
    }

    @GetMapping("/api-integrations")
    fun fetchApiIntegrationByName(@NotBlank @RequestParam(required = false) name: String?): Resource<OrganisationAccountResource> {
        val apiIntegration = getApiIntegrationByName(name!!)

        return Resource(
            organisationConverter.toResource(apiIntegration),
            listOfNotNull(
                organisationLinkBuilder.self(apiIntegration.id)
            )
        )
    }
}