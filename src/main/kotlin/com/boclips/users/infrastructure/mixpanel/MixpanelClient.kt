package com.boclips.users.infrastructure.mixpanel

import com.boclips.users.domain.model.analytics.Event
import com.boclips.users.domain.service.AnalyticsClient
import com.mixpanel.mixpanelapi.ClientDelivery
import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI

class MixpanelClient(mixpanelPropeties: MixpanelProperties, private val mixpanel: MixpanelAPI = MixpanelAPI()) : AnalyticsClient {
    private val messageBuilder = MessageBuilder(mixpanelPropeties.token)

    override fun track(event: Event) {
        ClientDelivery()
            .apply {
                addMessage(messageBuilder.event(event.userId, event.eventType.toString(), null))
            }
            .let {
                val useIpAddress = false
                mixpanel.deliver(it, useIpAddress)
            }
    }
}