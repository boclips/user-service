package com.boclips.users.presentation.hateoas

import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.presentation.controllers.OrganisationController
import com.boclips.users.presentation.controllers.OrganisationTestSupportController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Component

@Component
class OrganisationLinkBuilder {
    fun self(id: OrganisationAccountId): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(OrganisationTestSupportController::class.java).fetchOrganisationById(id.value)
        ).withSelfRel()
    }

    fun getSchoolLink(countryId: String?): Link? {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(OrganisationController::class.java).searchSchools(
                countryCode = countryId,
                query = null,
                state = null
            )
        ).withRel("schools")
    }

    fun getSchoolsAndDistrictsLink(countryCode: String): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(OrganisationController::class.java).getAllIndependentOrganisations(
                countryCode = countryCode
            )
        ).withRel("independentOrganisations")
    }
}
