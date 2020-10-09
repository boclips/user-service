package com.boclips.users.presentation.controllers

import com.boclips.users.api.request.CreateDistrictRequest
import com.boclips.users.api.request.OrganisationFilterRequest
import com.boclips.users.api.request.UpdateOrganisationRequest
import com.boclips.users.api.response.organisation.OrganisationResource
import com.boclips.users.api.response.organisation.OrganisationsResource
import com.boclips.users.api.response.user.UserResourceWrapper
import com.boclips.users.api.response.user.UsersResource
import com.boclips.users.application.commands.AssignUsersByOrganisationDomain
import com.boclips.users.application.commands.CreateDistrict
import com.boclips.users.application.commands.GetOrganisationById
import com.boclips.users.application.commands.GetOrganisations
import com.boclips.users.application.commands.SynchroniseIntegrationUser
import com.boclips.users.application.commands.UpdateOrganisation
import com.boclips.users.application.model.OrganisationFilter
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.presentation.converters.OrganisationConverter
import com.boclips.users.presentation.converters.UserConverter
import com.boclips.web.exceptions.ExceptionDetails
import com.boclips.web.exceptions.InvalidRequestApiException
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
    private val createDistrict: CreateDistrict,
    private val getOrganisations: GetOrganisations,
    private val assignUsersByOrganisationDomain: AssignUsersByOrganisationDomain,
    private val synchroniseIntegrationUser: SynchroniseIntegrationUser
) {

    @PostMapping("/organisations")
    fun create(
        @Valid @RequestBody createDistrictRequest: CreateDistrictRequest
    ): OrganisationResource {
        val newOrganisation = when (createDistrictRequest.type) {
            OrganisationType.DISTRICT.toString() -> createDistrict(createDistrictRequest)
            else -> throw InvalidRequestApiException(
                ExceptionDetails(
                    error = "Invalid organisation type",
                    message = "Only DISTRICT organisation is allowed"
                )
            )
        }
        return organisationConverter.toResource(newOrganisation)
    }

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
        val resources = assignUsersByOrganisationDomain(id).map(userConverter::toUserResource)
        return ResponseEntity.ok(
            UsersResource(
                _embedded = UserResourceWrapper(
                    resources
                )
            )
        )
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
