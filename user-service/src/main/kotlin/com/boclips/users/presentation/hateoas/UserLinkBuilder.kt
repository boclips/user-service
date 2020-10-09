package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor.currentUserHasAnyRole
import com.boclips.security.utils.UserExtractor.getCurrentUser
import com.boclips.security.utils.UserExtractor.getIfAuthenticated
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.user.UserId
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
            if (user?.hasDetailsHidden() == true || user?.hasOnboarded() == true)
                null
            else {
                profileLink(user?.id)?.withRel("activate")
            }
        }
    }

    fun createUserLink(): Link? {
        return if (getCurrentUser() == null)
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController::class.java)
                    .createAUser(null)
            ).withRel("createAccount")
        else null
    }

    fun profileLink(overrideUserId: UserId? = null): Link? {
        return getCurrentUser()?.let {
            val userId = overrideUserId?.value ?: it.id
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController::class.java).getAUser(userId)
            ).withRel("profile")
        }
    }

    fun currentUserLink(): Link? {
        return getCurrentUser()?.let {
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController::class.java).getSelf()
            ).withRel("currentUser")
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

    fun profileSelfLink(userId: UserId): Link? {
        return getIfAuthenticated {
            profileLink(userId)?.withSelfRel()
        }
    }

    fun accessRulesLink(userId: UserId? = null): Link? {
        return if (currentUserHasAnyRole(UserRoles.VIEW_ACCESS_RULES)) {
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController::class.java).fetchAccessRulesOfUser(
                    userId?.value
                )
            )
                .withRel("accessRules")
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
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(UserController::class.java).getShareCode(
                null,
                null
            )
        ).withRel("validateShareCode")
    }

    fun isUserActiveLink(): Link? {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(UserController::class.java).getIsUserActive(
                null
            )
        ).withRel("isUserActive")
    }
}
