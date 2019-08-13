package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor.getCurrentUserIfNotAnonymous
import com.boclips.security.utils.UserExtractor.getIfAuthenticated
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.presentation.controllers.UserController
import mu.KLogging
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Component

@Component
class UserLinkBuilder(private val userRepository: UserRepository) : KLogging() {

    fun activateUserLink(): Link? {
        return getIfAuthenticated { currentUserId ->
            if (userRepository.findById(UserId(value = currentUserId))?.activated == true)
                null
            else
                ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(UserController::class.java)
                        .activateAUser(currentUserId)
                ).withRel("activate")
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

    fun getUserLink(): Link? {
        return getIfAuthenticated {
            ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UserController::class.java)
                    .getAUser(null)
            ).withRel("profile")
        }
    }

    fun getUserLink(id: String): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(UserController::class.java)
                .getAUser(id)
        ).withRel("profile")
            .withSelfRel()
    }
}
