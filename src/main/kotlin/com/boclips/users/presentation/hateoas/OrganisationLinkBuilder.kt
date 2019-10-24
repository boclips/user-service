package com.boclips.users.presentation.hateoas

import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.presentation.controllers.OrganisationController
import com.boclips.users.presentation.controllers.OrganisationTestSupportController
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Component

@Component
class OrganisationLinkBuilder {
    fun self(id: OrganisationAccountId): Link {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(OrganisationTestSupportController::class.java).fetchOrganisationById(id.value)
        ).withSelfRel()
    }

    fun getSchoolLink(countryId: String?): Link? {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(OrganisationController::class.java).searchSchools(
                countryCode = countryId,
                query = null,
                state = null
            )
        ).withRel("schools")
    }
}
