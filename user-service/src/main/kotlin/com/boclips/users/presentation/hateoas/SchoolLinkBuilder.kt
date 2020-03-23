package com.boclips.users.presentation.hateoas

import com.boclips.users.presentation.controllers.SchoolController
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Service

@Service
class SchoolLinkBuilder {
    fun getSchoolLink(countryId: String?): Link? {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(SchoolController::class.java).searchSchools(
                countryCode = countryId,
                query = null,
                state = null
            )
        ).withRel("schools")
    }
}
