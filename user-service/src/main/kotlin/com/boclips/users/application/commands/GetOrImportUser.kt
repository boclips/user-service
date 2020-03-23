package com.boclips.users.application.commands

import com.boclips.users.application.UserImportService
import com.boclips.users.application.exceptions.IdentityNotFoundException
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.UserRepository
import org.springframework.stereotype.Service

@Service
class GetOrImportUser(
    private val userRepository: UserRepository,
    private val userImportService: UserImportService
) {
    operator fun invoke(userId: UserId): User {
        return userRepository.findById(id = userId) ?: importUser(userId)
    }

    private fun importUser(userId: UserId): User {
        return try {
            userImportService.importFromIdentityProvider(userId = userId)
        } catch (ex: IdentityNotFoundException) {
            throw UserNotFoundException(userId)
        }
    }
}
