package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.service.access.AccessExpiryService
import com.boclips.users.domain.service.events.EventService
import org.springframework.stereotype.Component

@Component
class TrackUserExpiredEvent(
    val getOrImportUser: GetOrImportUser,
    val accessExpiryService: AccessExpiryService,
    val eventService: EventService
) {

    operator fun invoke() {
        val user = getUser() ?: return

        if (!accessExpiryService.userHasAccess(user)) {
            eventService.publishUserExpired(user)
        }
    }

    private fun getUser(): User? {
        return UserExtractor.getCurrentUser()?.let { getOrImportUser(
            UserId(
                it.id
            )
        ) }
    }
}
