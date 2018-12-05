package com.boclips.users.domain.model.events

interface AnalyticsClient {
    fun track(event: Event)
}

