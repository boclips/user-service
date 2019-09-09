package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.school.Country
import com.boclips.users.presentation.controllers.CountryController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Service

@Service
class CountryLinkBuilder {
    fun getCountriesLink(): Link? {
        return UserExtractor.getIfAuthenticated {
            ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(CountryController::class.java).getAllCountries()
            ).withRel("countries")
        }
    }

    fun getCountriesSelfLink(): Link? {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(CountryController::class.java).getAllCountries()
        ).withSelfRel()
    }

    fun getUsStatesSelfLink(): Link? {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(CountryController::class.java).getAllUsStates()
        ).withSelfRel()
    }

    fun getStatesLink(country: Country): Link? {
        return when {
            country.isUSA() -> getUsStatesLink()
            else -> null
        }
    }

    private fun getUsStatesLink(): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(CountryController::class.java).getAllUsStates()
        ).withRel("states")
    }
}