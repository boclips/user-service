package com.boclips.users.infrastructure.mixpanel

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("mixpanel")
@Component
class MixpanelProperties {
    lateinit var token: String
}