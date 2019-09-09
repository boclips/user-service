package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor.currentUserHasAnyRole
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.UserId
import com.boclips.users.presentation.controllers.UserController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Service

@Service
class UserContractsLinkBuilder {
    fun self(userId: UserId): Link? {
        return if (currentUserHasAnyRole(UserRoles.VIEW_CONTRACTS)) {
            ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UserController::class.java).getContractsOfUser(userId.value)
            ).withRel("self")
        } else {
            null
        }
    }
}