package com.boclips.users.presentation.controllers

import com.boclips.users.api.response.organisation.OrganisationResource
import com.boclips.users.api.response.organisation.OrganisationsResource
import com.boclips.users.application.commands.AssignUsersByOrganisationDomain
import com.boclips.users.application.commands.GetOrganisationById
import com.boclips.users.application.commands.GetOrganisations
import com.boclips.users.application.commands.UpdateOrganisation
import com.boclips.users.application.model.OrganisationFilter
import com.boclips.users.presentation.requests.OrganisationFilterRequest
import com.boclips.users.presentation.requests.UpdateOrganisationRequest
import com.boclips.users.presentation.resources.UserResourceWrapper
import com.boclips.users.presentation.resources.UsersResource
import com.boclips.users.presentation.resources.converters.OrganisationConverter
import com.boclips.users.presentation.resources.converters.UserConverter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1", "/v1/")
class OrganisationController(
    private val getOrganisationById: GetOrganisationById,
    private val organisationConverter: OrganisationConverter,
    private val userConverter: UserConverter,
    private val updateOrganisation: UpdateOrganisation,
    private val getOrganisations: GetOrganisations,
    private val assignUsersByOrganisationDomain: AssignUsersByOrganisationDomain
) {

    @GetMapping("/organisations/{id}")
    fun fetchOrganisationById(@PathVariable("id") id: String?): OrganisationResource {
        val organisation = getOrganisationById(id!!)
        return organisationConverter.toResource(organisation)
    }

    @PatchMapping("/organisations/{id}")
    @Deprecated("Use the post mapping of this endpoint instead")
    fun updateProperty(
        @PathVariable id: String,
        @Valid @RequestBody updateOrganisationRequest: UpdateOrganisationRequest?
    ): OrganisationResource {
        return organisationConverter.toResource(updateOrganisation(id, updateOrganisationRequest))
    }

    @PostMapping("/organisations/{id}/associate")
    @Deprecated("This endpoint follows a bad practice as associate is not a rest resource. It serves a temporary need to associate orphan users.")
    fun assignUsers(@PathVariable id: String): ResponseEntity<UsersResource> {
        val organisation = getOrganisationById(id)
        val resources = assignUsersByOrganisationDomain(id).map { (userConverter.toUserResource(it, organisation)) }
        return ResponseEntity.ok(UsersResource(_embedded = UserResourceWrapper(resources)))
    }

    @PostMapping("/organisations/{id}")
    fun updateProperties(
        @PathVariable id: String,
        @Valid @RequestBody updateOrganisationRequest: UpdateOrganisationRequest?
    ): OrganisationResource {
        return organisationConverter.toResource(updateOrganisation(id, updateOrganisationRequest))
    }

    @GetMapping("/organisations")
    fun fetchAll(organisationFilterRequest: OrganisationFilterRequest?): OrganisationsResource {
        val filter = OrganisationFilter(
            name = organisationFilterRequest?.name,
            countryCode = organisationFilterRequest?.countryCode,
            page = organisationFilterRequest?.page ?: 0,
            size = organisationFilterRequest?.size ?: 30
        )
        val organisations = getOrganisations(filter)

        return organisationConverter.toResource(organisations)
    }
}