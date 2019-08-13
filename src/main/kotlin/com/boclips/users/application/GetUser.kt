package com.boclips.users.application

import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.presentation.resources.UserConverter
import com.boclips.users.presentation.resources.UserResource
import org.springframework.stereotype.Component

@Component
class GetUser(
    private val userRepository: UserRepository,
    private val userConverter: UserConverter,
    private val synchronisationService: SynchronisationService
) {
    operator fun invoke(requestedUserId: String): UserResource {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()
        if (authenticatedUser.id != requestedUserId) throw PermissionDeniedException()

        val userId = UserId(value = requestedUserId)
        val user = userRepository.findById(id = userId) ?: synchronisationService.synchronise(userId = userId)

        return userConverter.toUserResource(user)
    }
}
