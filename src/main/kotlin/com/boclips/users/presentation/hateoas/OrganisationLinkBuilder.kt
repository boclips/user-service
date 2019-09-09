package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor.getIfHasRole
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.presentation.controllers.OrganisationController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Component

@Component
class OrganisationLinkBuilder {
    fun self(id: OrganisationAccountId): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(OrganisationController::class.java).fetchOrganisationById(id.value)
        ).withSelfRel()
    }

    fun getApiIntegrationByName(): Link? {
        return getIfHasRole(UserRoles.VIEW_ORGANISATIONS) {
            ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(OrganisationController::class.java).fetchApiIntegrationByName(null)
            ).withRel("getApiIntegrationByName")
        }
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
}