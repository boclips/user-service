package com.boclips.users.application

import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.domain.service.SessionProvider
import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.service.userToCrmProfile
import org.springframework.stereotype.Component

@Component
class UpdateContacts(
    val userService: UserService,
    val customerManagementProvider: CustomerManagementProvider,
    val sessionProvider: SessionProvider
) {
    operator fun invoke() {
        val allCrmProfiles = userService.findAllUsers()
            .map { user ->
                val sessions = sessionProvider.getUserSessions(user.id)
                return@map userToCrmProfile(user, sessions)
            }

        customerManagementProvider.update(allCrmProfiles)
    }
}