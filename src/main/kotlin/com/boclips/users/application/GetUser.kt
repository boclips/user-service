package com.boclips.users.application

import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.service.UserService
import com.boclips.users.presentation.resources.UserConverter
import com.boclips.users.presentation.resources.UserResource
import org.springframework.stereotype.Component

@Component
class GetUser(
    private val userService: UserService
) {
    operator fun invoke(requestedUserId: String): UserResource {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()

        if (authenticatedUser.id != requestedUserId) throw PermissionDeniedException()

        val user = userService.findById(id = IdentityId(value = requestedUserId))

        return UserConverter().toUserResource(user)
    }
}