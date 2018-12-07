package com.boclips.users.domain.model.events

data class Event(
        val eventType: EventType,
        val userId: String
)

enum class EventType {
    ACCOUNT_CREATED
}