package com.boclips.users.infrastructure.mixpanel

import com.boclips.users.domain.model.events.AnalyticsClient
import com.boclips.users.domain.model.events.Event
import com.mixpanel.mixpanelapi.ClientDelivery
import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI

class MixpanelClient(mixpanelPropeties: MixpanelProperties) : AnalyticsClient {
    private val mixpanel = MixpanelAPI()
    private val messageBuilder = MessageBuilder(mixpanelPropeties.token)

    override fun track(event: Event) {
        ClientDelivery()
                .apply {
                    addMessage(messageBuilder.event(event.userId, event.eventType.toString(), null))
                }
                .let { mixpanel.deliver(it) }
    }
}