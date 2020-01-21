package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor.currentUserHasAnyRole
import com.boclips.security.utils.UserExtractor.getCurrentUserIfNotAnonymous
import com.boclips.security.utils.UserExtractor.getIfAuthenticated
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.presentation.controllers.EventController
import com.boclips.users.presentation.controllers.UserController
import mu.KLogging
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Component

@Component
class UserLinkBuilder :
    KLogging() {

    fun activateUserLink(user: User?): Link? {
        return getIfAuthenticated {
            if (user?.hasOnboarded() == true)
                null
            else {
                profileLink()?.withRel("activate")
            }
        }
    }

    fun createUserLink(): Link? {
        return if (getCurrentUserIfNotAnonymous() == null)
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController::class.java)
                    .createAUser(null)
            ).withRel("createAccount")
        else null
    }

    fun profileLink(): Link? {
        return getCurrentUserIfNotAnonymous()?.let {
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController::class.java).getAUser(it.id)
            ).withRel("profile")
        }
    }

    fun newUserProfileLink(userId: UserId): Link? =
        WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder
                .methodOn(UserController::class.java).getAUser(userId.value)
        ).withRel("profile")

    fun userLink(): Link? {
        return if (currentUserHasAnyRole(UserRoles.VIEW_USERS)) {
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController::class.java).getAUser(null))
                .withRel("user")
        } else {
            null
        }
    }

    fun profileSelfLink(): Link? {
        return getIfAuthenticated {
            profileLink()?.withSelfRel()
        }
    }

    fun contractsLink(userId: UserId? = null): Link? {
        return if (currentUserHasAnyRole(UserRoles.VIEW_CONTRACTS)) {
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController::class.java).getContractsOfUser(
                    userId?.value
                )
            )
                .withRel("contracts")
        } else {
            null
        }
    }

    fun reportAccessExpiredLink(user: User?, hasAccess: Boolean): Link? {
        return getIfAuthenticated {
            if (user?.let { hasAccess } != false) {
                null
            } else {
                WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(EventController::class.java).trackUserExpiredEvent()
                )
                    .withRel("reportAccessExpired")
            }
        }
    }

    fun validateShareCodeLink(): Link? {
        return if (getCurrentUserIfNotAnonymous() == null) {
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController::class.java).checkUserShareCode(null, null)
            ).withRel("validateShareCode")
        } else {
            null
        }
    }
}
