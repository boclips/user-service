package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.UserId
import org.springframework.stereotype.Component

@Component
class GetTrackableUserId(
    val getOrImportUser: GetOrImportUser
) {
    companion object {
        const val ANONYMOUS_USER_ID = "anonymousUser"
    }

    operator fun invoke(): String {
        return UserExtractor.getCurrentUser()?.let {
            val user = getOrImportUser(UserId(it.id))
            if (user.hasOnboarded()) user.id.value else ANONYMOUS_USER_ID
        } ?: ANONYMOUS_USER_ID
    }
}