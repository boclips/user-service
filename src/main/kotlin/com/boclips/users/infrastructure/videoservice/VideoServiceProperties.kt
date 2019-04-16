package com.boclips.users.infrastructure.videoservice

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("video-service")
@Component
class VideoServiceProperties {
    lateinit var baseUrl: String
}