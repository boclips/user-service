package com.boclips.users.domain.model.analytics

data class Event(
    val eventType: EventType,
    val userId: String
)
