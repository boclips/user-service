package com.boclips.users.application

import com.boclips.security.utils.UserExtractor
import com.boclips.security.utils.UserExtractor.currentUserHasRole
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.AccountNotFoundException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.presentation.resources.UserConverter
import com.boclips.users.presentation.resources.UserResource
import org.springframework.stereotype.Component

@Component
class GetUser(
    private val userRepository: UserRepository,
    private val userConverter: UserConverter,
    private val userImportService: UserImportService
) {
    operator fun invoke(requestedUserId: String): UserResource {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()
        val isOwnProfile = authenticatedUser.id == requestedUserId
        if (!(currentUserHasRole(UserRoles.VIEW_USERS) || isOwnProfile)) {
            throw PermissionDeniedException()
        }

        val userId = UserId(value = requestedUserId)

        val user = userRepository.findById(id = userId) ?: importUser(userId)

        return userConverter.toUserResource(user)
    }

    private fun importUser(userId: UserId): User {
        return try {
            userImportService.importFromAccountProvider(userId = userId)
        } catch (ex: AccountNotFoundException) {
            throw UserNotFoundException(userId)
        }
    }
}
