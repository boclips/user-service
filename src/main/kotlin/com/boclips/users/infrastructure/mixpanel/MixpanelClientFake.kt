package com.boclips.users.infrastructure.mixpanel

import com.boclips.users.domain.model.events.AnalyticsClient
import com.boclips.users.domain.model.events.Event

class MixpanelClientFake: AnalyticsClient {
    private val events: MutableList<Event> = mutableListOf()

    override fun track(event: Event) {
        events.add(event)
    }

    fun getEvents(): List<Event> = events
    fun clear() {
        events.clear()
    }
}