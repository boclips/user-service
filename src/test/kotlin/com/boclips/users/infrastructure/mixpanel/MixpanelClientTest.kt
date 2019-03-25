package com.boclips.users.infrastructure.mixpanel

import com.boclips.users.domain.model.analytics.Event
import com.boclips.users.domain.model.analytics.EventType
import com.mixpanel.mixpanelapi.MixpanelAPI
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test

class MixpanelClientTest {

    @Test
    fun `indicates the server's IP should not be recorded as the user's location`() {
        val mixpanelAPI = mock<MixpanelAPI>()
        val mixpanelProperties = MixpanelProperties().apply { token = "xyz" }
        val mixpanelClient = MixpanelClient(mixpanelProperties, mixpanelAPI)

        val event = Event(eventType = EventType.ACCOUNT_CREATED, userId = "123")
        mixpanelClient.track(event)

        verify(mixpanelAPI).deliver(any(), eq(false))
    }
}