package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.account.OrganisationId
import com.boclips.users.presentation.controllers.AccountController
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Component

@Component
class AccountLinkBuilder(private val uriComponentsBuilderFactory: UriComponentsBuilderFactory) {
    fun self(id: OrganisationId): Link {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(AccountController::class.java).fetchOrganisationById(id.value)
        ).withSelfRel()
    }

    fun getAccountLink(): Link? {
        return if (UserExtractor.currentUserHasAnyRole(UserRoles.VIEW_ORGANISATIONS)) {
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(AccountController::class.java).fetchOrganisationById(null)
            ).withRel("account")
        } else {
            null
        }
    }

    fun edit(id: OrganisationId): Link {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(AccountController::class.java).updateAnAccount(id.value, null)
        ).withRel("edit")
    }

    fun getIndependentAccountsLink(): Link? {
        return if (UserExtractor.currentUserHasAnyRole(UserRoles.VIEW_ORGANISATIONS)) {
            Link(
                uriComponentsBuilderFactory.getInstance()
                    .replacePath("/v1/accounts")
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
