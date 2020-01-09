package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor.currentUserHasAnyRole
import com.boclips.security.utils.UserExtractor.getCurrentUserIfNotAnonymous
import com.boclips.security.utils.UserExtractor.getIfAuthenticated
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.AccessService
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.presentation.controllers.EventController
import com.boclips.users.presentation.controllers.UserController
import mu.KLogging
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Component

@Component
class UserLinkBuilder(private val userRepository: UserRepository, private val accessService: AccessService) :
    KLogging() {

    fun activateUserLink(): Link? {
        return getIfAuthenticated { currentUserId ->
            if (userRepository.findById(UserId(value = currentUserId))?.hasOnboarded() == true)
                null
            else {
                profileLink()?.withRel("activate")
            }
        }
    }

    fun createUserLink(): Link? {
        return if (getCurrentUserIfNotAnonymous() == null)
            ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UserController::class.java)
                    .createAUser(null)
            ).withRel("createAccount")
        else null
    }

    fun profileLink(): Link? {
        return getCurrentUserIfNotAnonymous()?.let {
            ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UserController::class.java).getAUser(it.id)
            ).withRel("profile")
        }
    }

    fun newUserProfileLink(userId: UserId): Link? =
        ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder
                .methodOn(UserController::class.java).getAUser(userId.value)
        ).withRel("profile")

    fun userLink(): Link? {
        return if (currentUserHasAnyRole(UserRoles.VIEW_USERS)) {
            ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UserController::class.java).getAUser(null))
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
            ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UserController::class.java).getContractsOfUser(
                    userId?.value
                )
            )
                .withRel("contracts")
        } else {
            null
        }
    }

    fun reportAccessExpiredLink(): Link? {
        return getIfAuthenticated { currentUserId ->
            if (userRepository.findById(UserId(value = currentUserId))?.let { accessService.userHasAccess(it) } != false) {
                null
            } else {
                ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(EventController::class.java).trackUserExpiredEvent()
                )
                    .withRel("reportAccessExpired")
            }
        }
    }

    fun validateShareCodeLink(): Link? {
        return if (getCurrentUserIfNotAnonymous() == null) {
            ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UserController::class.java).checkUserShareCode(null, null)
            ).withRel("validateShareCode")
        } else {
            null
        }
    }
}
