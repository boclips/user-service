package com.boclips.users.infrastructure.security

import com.boclips.security.utils.User
import com.boclips.security.utils.UserExtractor

fun getCurrentUserIfNotAnonymous(): User? {
    val user = UserExtractor.getCurrentUser()

    if (user?.id == "anonymousUser") {
        return null
    }

    return user
}