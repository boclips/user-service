package com.boclips.users.application.commands

import com.boclips.users.domain.service.events.EventService
import org.springframework.stereotype.Component

@Component
class TrackPlatformInteractedWithAnonymouslyEvent(
    val eventService: EventService
) {
    operator fun invoke(subtype: String, url: String?) {
        eventService.publishPlatformInteractedWithAnonymously(subtype = subtype, url = url)
    }
}
