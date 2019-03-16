package com.boclips.users.infrastructure.referralrock

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("referralrock")
@Component
class ReferralRockProperties {
    lateinit var host: String
    lateinit var publicKey: String
    lateinit var privateKey: String
}
