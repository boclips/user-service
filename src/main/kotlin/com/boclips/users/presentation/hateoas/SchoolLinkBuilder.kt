package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor
import com.boclips.users.presentation.controllers.SchoolController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Component

@Component
class SchoolLinkBuilder {
    fun getCountriesLink(): Link? {
        return UserExtractor.getIfAuthenticated {
            ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(SchoolController::class.java).getAllCountries()
            ).withRel("countries")
        }
    }

    fun getCountriesSelfLink(): Link? {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(SchoolController::class.java).getAllCountries()
        ).withSelfRel()
    }

    fun getUsStatesSelfLink(): Link? {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(SchoolController::class.java).getAllUsStates()
        ).withSelfRel()
    }
}