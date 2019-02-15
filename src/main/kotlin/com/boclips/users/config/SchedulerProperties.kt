package com.boclips.users.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("app")
@Component
class SchedulerProperties {
    lateinit var registrationPeriodInMillis: String
}