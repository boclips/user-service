package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.GetOrganisationById
import com.boclips.users.application.commands.GetOrganisations
import com.boclips.users.application.commands.UpdateOrganisation
import com.boclips.users.application.model.OrganisationFilter
import com.boclips.users.presentation.requests.OrganisationFilterRequest
import com.boclips.users.presentation.requests.UpdateOrganisationRequest
import com.boclips.users.presentation.resources.OrganisationResource
import com.boclips.users.presentation.resources.converters.OrganisationConverter
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1", "/v1/")
class OrganisationController(
    private val getOrganisationById: GetOrganisationById,
    private val organisationConverter: OrganisationConverter,
    private val updateOrganisation: UpdateOrganisation,
    private val getOrganisations: GetOrganisations
) {

    @GetMapping("/organisations/{id}")
    fun fetchOrganisationById(@PathVariable("id") id: String?): EntityModel<OrganisationResource> {
        val organisation = getOrganisationById(id!!)
        return organisationConverter.toResource(organisation)
    }

    @GetMapping("/accounts/{id}")
    fun fetchAccountById(@PathVariable("id") id: String?): EntityModel<OrganisationResource> {
        return fetchOrganisationById(id)
    }

    @PatchMapping("/organisations/{id}")
    fun update(@PathVariable id: String, @Valid @RequestBody updateOrganisationRequest: UpdateOrganisationRequest?): EntityModel<OrganisationResource> {
        return organisationConverter.toResource(updateOrganisation(id, updateOrganisationRequest))
    }

    @PatchMapping("/accounts/{id}")
    fun updateAnAccount(@PathVariable id: String, @Valid @RequestBody updateOrganisationRequest: UpdateOrganisationRequest?): EntityModel<OrganisationResource> {
        return update(id, updateOrganisationRequest)
    }

    @GetMapping("/organisations")
    fun fetchAll(organisationFilterRequest: OrganisationFilterRequest?): PagedModel<EntityModel<OrganisationResource>> {
        val filter = OrganisationFilter(
            countryCode = organisationFilterRequest?.countryCode,
            page = organisationFilterRequest?.page ?: 0,
            size = organisationFilterRequest?.size ?: 30
        )
        val accounts = getOrganisations(filter)

        val accountResources = accounts.map { account -> organisationConverter.toResource(account) }

        return PagedModel(
            accountResources.content,
            PagedModel.PageMetadata(
                filter.size.toLong(),
                filter.page.toLong(),
                accountResources.totalElements
            )
        )
    }

    @GetMapping("/accounts")
    fun listAccounts(organisationFilterRequest: OrganisationFilterRequest?): PagedModel<EntityModel<OrganisationResource>> {
        return fetchAll(organisationFilterRequest)
    }
}
