package com.boclips.users.application.commands

import com.boclips.users.domain.service.events.EventService
import org.springframework.stereotype.Component

@Component
class TrackPlatformInteractedWithEvent(
    val eventService: EventService,
    val getTrackableUserId: GetTrackableUserId
) {
    operator fun invoke(subtype: String, url: String?, anonymous: Boolean?) {
        if (anonymous != null && anonymous == true) {
            eventService.publishPlatformInteractedWithAnonymously(subtype = subtype, url = url)
        } else {
            val userId = getTrackableUserId()
            eventService.publishPlatformInteractedWith(userId = userId, subtype = subtype, url = url)
        }
    }
}
