package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.ShareCodeNotFoundException
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.user.UserRepository
import org.springframework.stereotype.Component

@Component
class ValidateShareCode(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String, shareCode: String): Boolean {
        val user = userRepository.findById(UserId(userId))
        user?.let {
            if (user.shareCode != null) {
                return user.shareCode.equals(shareCode, true)
            } else {
                throw ShareCodeNotFoundException(user.id)
            }
        }
        throw UserNotFoundException(UserId(userId))
    }
}
