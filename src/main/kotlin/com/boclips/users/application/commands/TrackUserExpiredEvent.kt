package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.AccessService
import com.boclips.users.domain.service.events.EventService
import org.springframework.stereotype.Component

@Component
class TrackUserExpiredEvent(
    val getOrImportUser: GetOrImportUser,
    val accessService: AccessService,
    val eventService: EventService
) {

    operator fun invoke() {
        val user = getUser() ?: return

        if (!accessService.userHasAccess(user)) {
            eventService.publishUserExpired(user)
        }
    }

    private fun getUser(): com.boclips.users.domain.model.User? {
        return UserExtractor.getCurrentUser()?.let { getOrImportUser(UserId(it.id)) }
    }
}