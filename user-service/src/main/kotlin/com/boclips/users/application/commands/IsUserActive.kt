package com.boclips.users.application.commands

import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.user.UserRepository
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class IsUserActive(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String): Boolean {
        return userRepository.findById(UserId(userId)).let {user ->
            when (user) {
                is User -> user.accessExpiresOn?.let { ZonedDateTime.now().isBefore(it) } ?: false
                else -> false
            }
        }
    }
}
