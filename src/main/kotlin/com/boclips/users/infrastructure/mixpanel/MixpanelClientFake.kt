package com.boclips.users.infrastructure.mixpanel

import com.boclips.users.domain.service.AnalyticsClient
import com.boclips.users.domain.model.Event

class MixpanelClientFake : AnalyticsClient {
    private val events: MutableList<Event> = mutableListOf()

    override fun track(event: Event) {
        events.add(event)
    }

    fun getEvents(): List<Event> = events
    fun clear() {
        events.clear()
    }
}