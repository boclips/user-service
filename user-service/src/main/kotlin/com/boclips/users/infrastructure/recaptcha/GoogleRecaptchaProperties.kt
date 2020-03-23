package com.boclips.users.infrastructure.recaptcha

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("googlerecaptcha")
@Component
class GoogleRecaptchaProperties {
    lateinit var host: String
    lateinit var secretKey: String
    var threshold: Double = 0.5
}
