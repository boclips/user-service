package com.boclips.users.presentation.hateoas

import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.presentation.controllers.OrganisationController
import com.boclips.users.presentation.controllers.OrganisationTestSupportController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Component

@Component
class OrganisationLinkBuilder(private val uriComponentsBuilderFactory: UriComponentsBuilderFactory) {
    fun self(id: OrganisationAccountId): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(OrganisationTestSupportController::class.java).fetchOrganisationById(id.value)
        ).withSelfRel()
    }

    fun edit(id: OrganisationAccountId): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(OrganisationController::class.java).updateAnOrganisation(id.value, null)
        ).withRel("edit")
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

    fun getIndependentOrganisationsLink(countryCode: String, page: Int = 0, size: Int = 30): Link {
        return Link(
            uriComponentsBuilderFactory.getInstance()
                .replacePath("/v1/independent-organisations")
                .replaceQueryParams(null)
                .queryParam("countryCode", countryCode)
                .queryParam("page", page)
                .queryParam("size", size)
                .toUriString(),
            "independentOrganisations"
        )
    }

    fun getNextPageLink(currentPage: Int, totalPages: Int): Link? {
        return if (currentPage +1 < totalPages) {
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
