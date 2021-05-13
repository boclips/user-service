package com.boclips.users.application.commands

import com.boclips.eventbus.domain.page.Viewport
import com.boclips.users.api.request.PageRenderedEventRequest
import com.boclips.users.domain.service.events.EventService
import org.springframework.stereotype.Component

@Component
class TrackPageRenderedEvent(
    val eventService: EventService,
    val getTrackableUserId: GetTrackableUserId
) {

    operator fun invoke(request: PageRenderedEventRequest) {
        val userId = getTrackableUserId()
        eventService.publishPageRendered(
            userId = userId,
            url = request.url,
            viewport = request.viewport?.let { Viewport(it.width, it.height) },
            isResize = request.isResize
        )
    }
}
