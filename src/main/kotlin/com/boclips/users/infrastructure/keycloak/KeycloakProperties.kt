package com.boclips.users.infrastructure.keycloak

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("keycloak")
@Component
class KeycloakProperties {
    lateinit var url: String
    lateinit var username: String
    lateinit var password: String
}