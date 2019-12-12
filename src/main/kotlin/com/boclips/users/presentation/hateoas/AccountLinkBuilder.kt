package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.account.OrganisationAccountId
import com.boclips.users.presentation.controllers.AccountController
import com.boclips.users.presentation.controllers.AccountTestSupportController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Component

@Component
class AccountLinkBuilder(private val uriComponentsBuilderFactory: UriComponentsBuilderFactory) {
    fun self(id: OrganisationAccountId): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(AccountTestSupportController::class.java).fetchOrganisationById(id.value)
        ).withSelfRel()
    }

    fun edit(id: OrganisationAccountId): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(AccountController::class.java).updateAnAccount(id.value, null)
        ).withRel("edit")
    }

    fun getIndependentOrganisationsLink(): Link? {
        return if (UserExtractor.currentUserHasAnyRole(UserRoles.VIEW_ORGANISATIONS)) {
            Link(
                uriComponentsBuilderFactory.getInstance()
                    .replacePath("/v1/independent-accounts")
                    .replaceQueryParams(null)
                    .toUriString() + "{?countryCode,page,size}",
                "independentAccounts"
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
