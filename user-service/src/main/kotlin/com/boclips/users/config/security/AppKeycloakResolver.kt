package com.boclips.users.config.security

import com.boclips.users.infrastructure.keycloak.KeycloakProperties
import org.keycloak.adapters.KeycloakConfigResolver
import org.keycloak.adapters.KeycloakDeployment
import org.keycloak.adapters.spi.HttpFacade
import org.keycloak.common.enums.SslRequired
import org.keycloak.representations.adapters.config.AdapterConfig
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

class AppKeycloakConfigResolver(private val keycloakProperties: KeycloakProperties) : KeycloakConfigResolver {
    init {
        assert(keycloakProperties.realm.isNotBlank())
        assert(keycloakProperties.url.isNotBlank())
    }

    override fun resolve(facade: HttpFacade.Request?): KeycloakDeployment =
        KeycloakDeployment().apply {
            isBearerOnly = true
            sslRequired = SslRequired.EXTERNAL
            confidentialPort = 0
            isUseResourceRoleMappings = true

            resourceName = "user-service"
            realm = keycloakProperties.realm
            setAuthServerBaseUrl(AdapterConfig().apply { authServerUrl = keycloakProperties.url })
        }
}
