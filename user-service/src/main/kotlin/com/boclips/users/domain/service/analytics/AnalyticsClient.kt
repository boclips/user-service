package com.boclips.users.domain.service.analytics

import com.boclips.users.domain.model.analytics.Event

interface AnalyticsClient {
    fun track(event: Event)
}
