package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.presentation.controllers.OrganisationController
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Component

@Component
class OrganisationLinkBuilder(private val uriComponentsBuilderFactory: UriComponentsBuilderFactory) {
    fun self(id: OrganisationId): Link {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(OrganisationController::class.java).fetchOrganisationById(id.value)
        ).withSelfRel()
    }

    fun getOrganisationLink(): Link? {
        return if (UserExtractor.currentUserHasAnyRole(UserRoles.VIEW_ORGANISATIONS)) {
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(OrganisationController::class.java).fetchOrganisationById(null)
            ).withRel("organisation")
        } else {
            null
        }
    }

    fun edit(id: OrganisationId): Link {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(OrganisationController::class.java).update(id.value, null)
        ).withRel("edit")
    }

    fun getOrganisationsLink(): Link? {
        return if (UserExtractor.currentUserHasAnyRole(UserRoles.VIEW_ORGANISATIONS)) {
            Link(
                uriComponentsBuilderFactory.getInstance()
                    .replacePath("/v1/organisations")
                    .replaceQueryParams(null)
                    .toUriString() + "{?countryCode,page,size}",
                "organisations"
            )
        } else {
            null
        }
    }

    fun getNextPageLink(currentPage: Int, totalPages: Int): Link? {
        return if (currentPage + 1 < totalPages) {
            Link(
                uriComponentsBuilderFactory
                    .getInstance()
                    .replaceQueryParam("page", currentPage + 1)
                    .toUriString(),
                "next"
            )
        } else {
            null
        }
    }
}
