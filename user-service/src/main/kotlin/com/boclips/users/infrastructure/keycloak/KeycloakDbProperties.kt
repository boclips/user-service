package com.boclips.users.infrastructure.keycloak

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("keycloak.db")
@Component
class KeycloakDbProperties {
    var connectionName: String = ""
    var username: String = ""
    var password: String = ""
    var dbName: String = "keycloak"
    var proxyPort: String = "5432"
}
