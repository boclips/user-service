package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor.currentUserHasAnyRole
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.presentation.controllers.UserController
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Service

@Service
class UserAccessRulesLinkBuilder {
    fun self(userId: UserId): Link? {
        return if (currentUserHasAnyRole(UserRoles.VIEW_ACCESS_RULES)) {
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController::class.java).fetchAccessRulesOfUser(userId.value, null)
            ).withRel("self")
        } else {
            null
        }
    }
}
