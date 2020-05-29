package com.boclips.users.domain.service.events

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.page.PageRendered
import com.boclips.eventbus.events.platform.PlatformInteractedWith
import com.boclips.eventbus.events.user.UserExpired
import com.boclips.users.domain.model.user.User
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

    fun publishPlatformInteractedWith(userId: String, subtype: String, url: String?) {
        eventBus.publish(
            PlatformInteractedWith.builder()
                .userId(userId)
                .subtype(subtype)
                .url(url)
                .build()
        )
    }

    fun publishPlatformInteractedWithAnonymously(subtype: String, url: String?) {
        eventBus.publish(
            PlatformInteractedWith.builder()
                .subtype(subtype)
                .url(url)
                .build()
        )
    }
}
