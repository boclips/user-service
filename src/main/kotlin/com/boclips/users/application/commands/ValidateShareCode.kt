package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.ShareCodeNotFoundException
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.UserRepository
import org.springframework.stereotype.Component

@Component
class ValidateShareCode(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String, shareCode: String): Boolean {
        val user = userRepository.findById(UserId(userId))
        user?.let {
            if (user.teacherPlatformAttributes?.shareCode != null) {
                return user.teacherPlatformAttributes.shareCode == shareCode
            } else {
                throw ShareCodeNotFoundException(user.id)
            }
        }
        throw UserNotFoundException(UserId(userId))
    }
}
