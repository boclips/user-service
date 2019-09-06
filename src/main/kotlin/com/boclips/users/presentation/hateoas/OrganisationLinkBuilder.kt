package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor
import com.boclips.security.utils.UserExtractor.getIfHasRole
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.presentation.controllers.OrganisationController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Component

@Component
class OrganisationLinkBuilder {
    fun self(id: OrganisationAccountId): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(OrganisationController::class.java).fetchOrganisation(id.value)
        ).withSelfRel()
    }

    fun getOrganisationByName(): Link? {
        return getIfHasRole(UserRoles.VIEW_ORGANISATIONS) {
            ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(OrganisationController::class.java).fetchOrganisationByName(null)
            ).withRel("getOrganisationByName")
        }
    }


    fun getCountriesLink(): Link? {
        return UserExtractor.getIfAuthenticated {
            ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(OrganisationController::class.java).getAllCountries()
            ).withRel("countries")
        }
    }

    fun getCountriesSelfLink(): Link? {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(OrganisationController::class.java).getAllCountries()
        ).withSelfRel()
    }

    fun getUsStatesSelfLink(): Link? {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(OrganisationController::class.java).getAllUsStates()
        ).withSelfRel()
    }

    fun getSchoolLink(countryId: String?): Link? {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(OrganisationController::class.java).searchSchools(
                country = countryId,
                query = null,
                state = null
            )
        ).withRel("schools")
    }

    fun getStatesLink(country: Country): Link? {
        return when {
            country.isUSA() -> getUsStatesLink()
            else -> null
        }
    }

    private fun getUsStatesLink(): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(OrganisationController::class.java).getAllUsStates()
        ).withRel("states")
    }
}