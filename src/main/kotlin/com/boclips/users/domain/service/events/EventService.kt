package com.boclips.users.domain.service.events

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.page.PageRendered
import com.boclips.eventbus.events.user.UserExpired
import com.boclips.users.domain.model.User
import org.springframework.stereotype.Service

@Service
class EventService(
    val eventBus: EventBus,
    val eventConverter: EventConverter
) {
    fun publishPageRendered(userId: String, url: String) {
        eventBus.publish(
            PageRendered.builder()
                .userId(userId)
                .url(url)
                .build()
        )
    }

    fun publishUserExpired(user: User) {
        eventBus.publish(
            UserExpired.builder()
                .user(eventConverter.toEventUser(user))
                .build()
        )
    }
}
