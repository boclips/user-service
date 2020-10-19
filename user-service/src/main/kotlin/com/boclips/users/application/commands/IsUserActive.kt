package com.boclips.users.application.commands

import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.service.access.AccessExpiryService
import org.springframework.stereotype.Component

@Component
class IsUserActive(
    private val userRepository: UserRepository,
    private val accessExpiryService: AccessExpiryService
) {
    operator fun invoke(userId: String): Boolean {
        return userRepository.findById(UserId(userId))?.let { user ->
            accessExpiryService.userHasAccess(user)
        } ?: false
    }
}
