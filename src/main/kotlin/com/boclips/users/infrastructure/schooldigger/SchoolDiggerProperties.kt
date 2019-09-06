package com.boclips.users.infrastructure.schooldigger

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("schooldigger")
@Component
class SchoolDiggerProperties {
    lateinit var host: String
    lateinit var applicationId: String
    lateinit var applicationKey: String
}