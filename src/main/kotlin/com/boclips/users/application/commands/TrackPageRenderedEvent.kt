package com.boclips.users.application.commands

import com.boclips.security.utils.User
import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.events.EventService
import com.boclips.users.presentation.requests.PageRenderedEventRequest
import org.springframework.stereotype.Component

@Component
class TrackPageRenderedEvent(
    val eventService: EventService,
    val getOrImportUser: GetOrImportUser
) {

    companion object {
        const val ANONYMOUS_USER_ID = "anonymousUser"
    }

    operator fun invoke(request: PageRenderedEventRequest) {
        val authenticatedUser = getAuthenticatedUser()

        val userId = if (canTrackUser(authenticatedUser.id)) {
            authenticatedUser.id
        } else {
            ANONYMOUS_USER_ID
        }

        eventService.publishPageRendered(userId = userId, url = request.url)
    }

    private fun getAuthenticatedUser(): User {
        return UserExtractor.getCurrentUser() ?: User(false, ANONYMOUS_USER_ID, emptySet())
    }

    private fun canTrackUser(userId: String): Boolean = if (ANONYMOUS_USER_ID != userId) {
        val user = getOrImportUser.invoke(UserId(userId))
        user.isOnboarded()
    } else {
        false
    }
}
