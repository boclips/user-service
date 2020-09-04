package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor
import com.boclips.users.api.response.user.UserResource
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.presentation.converters.UserConverter
import org.springframework.stereotype.Component

@Component
class GetSelfUser(
    private val userConverter: UserConverter,
    private val getOrImportUser: GetOrImportUser
) {
    operator fun invoke(): UserResource {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()
        val userId = UserId(value = authenticatedUser.id)
        val user = getOrImportUser(userId)

        return userConverter.toUserResource(user)
    }
}
