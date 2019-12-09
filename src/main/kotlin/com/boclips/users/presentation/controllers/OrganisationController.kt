package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.GetIndependentOrganisations
import com.boclips.users.application.commands.SearchSchools
import com.boclips.users.application.commands.UpdateOrganisation
import com.boclips.users.infrastructure.organisation.OrganisationSearchRequest
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.presentation.requests.UpdateOrganisationRequest
import com.boclips.users.presentation.resources.OrganisationAccountResource
import com.boclips.users.presentation.resources.converters.OrganisationAccountConverter
import com.boclips.users.presentation.resources.school.SchoolResource
import org.springframework.hateoas.PagedResources
import org.springframework.hateoas.Resource
import org.springframework.hateoas.Resources
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1", "/v1/")
class OrganisationController(
    private val searchSchools: SearchSchools,
    private val getIndependentOrganisations: GetIndependentOrganisations,
    private val organisationAccountConverter: OrganisationAccountConverter,
    private val updateOrganisation: UpdateOrganisation,
    private val organisationLinkBuilder: OrganisationLinkBuilder
) {
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

    @GetMapping("/independent-organisations")
    fun getAllIndependentOrganisations(
        @RequestParam(required = true) countryCode: String?,
        @RequestParam(required = false) page: Int? = null,
        @RequestParam(required = false) size: Int? = null
    ): PagedResources<Resource<OrganisationAccountResource>> {
        val organisationAccounts =
            getIndependentOrganisations(OrganisationSearchRequest(countryCode = countryCode!!, page = page, size = size))

        val organisationAccountResources = organisationAccounts.map { account -> organisationAccountConverter.toResource(account) }

        return PagedResources(
            organisationAccountResources.content,
            PagedResources.PageMetadata(size?.toLong() ?: 30, page?.toLong() ?: 0, organisationAccounts.totalElements),
            listOfNotNull(organisationLinkBuilder.getNextPageLink(page ?: 0, organisationAccounts.totalPages))
        )
    }

    @PutMapping("organisations/{id}")
    fun updateAnOrganisation(@PathVariable id: String, @Valid @RequestBody updateOrganisationRequest: UpdateOrganisationRequest?): Resource<OrganisationAccountResource> {
        return organisationAccountConverter.toResource(updateOrganisation(id, updateOrganisationRequest))
    }
}
