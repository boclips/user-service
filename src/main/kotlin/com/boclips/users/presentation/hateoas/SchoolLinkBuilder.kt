package com.boclips.users.presentation.hateoas

import com.boclips.users.presentation.controllers.SchoolController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Service

@Service
class SchoolLinkBuilder {
    fun getSchoolLink(countryId: String?): Link? {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(SchoolController::class.java).searchSchools(
                countryCode = countryId,
                query = null,
                state = null
            )
        ).withRel("schools")
    }
}
