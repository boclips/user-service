package com.boclips.users.infrastructure.hubspot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("hubspot")
@Component
class HubSpotProperties {
    lateinit var host: String
    lateinit var apiKey: String
    var batchSize: Int = 100
    var marketingSubscriptionId: Long = -1
}
