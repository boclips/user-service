package com.boclips.users.domain.service

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.page.PageRendered
import org.springframework.stereotype.Service

@Service
class EventService(
    val eventBus: EventBus
) {
    fun publishPageRendered(userId: String, url: String) {
        eventBus.publish(PageRendered.builder()
            .userId(userId)
            .url(url)
            .build())
    }
}
