package com.boclips.users.config

import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.domain.service.MetadataProvider
import com.boclips.users.infrastructure.hubspot.HubSpotClient
import com.boclips.users.infrastructure.hubspot.HubSpotProperties
import com.boclips.users.infrastructure.keycloak.KeycloakProperties
import com.boclips.users.infrastructure.keycloak.client.KeycloakClient
import com.boclips.users.infrastructure.keycloak.client.KeycloakUserToUserIdentityConverter
import com.boclips.users.infrastructure.keycloak.metadata.KeycloakMetadataProvider
import com.boclips.users.infrastructure.mixpanel.MixpanelClient
import com.boclips.users.infrastructure.mixpanel.MixpanelProperties
import com.fasterxml.jackson.databind.ObjectMapper
import org.keycloak.admin.client.Keycloak
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class ContextConfig(
    val objectMapper: ObjectMapper
) {
    @Bean
    fun analyticsClient(properties: MixpanelProperties) = MixpanelClient(properties)

    @Bean
    fun identityProvider(keycloak: Keycloak) = KeycloakClient(
        keycloak,
        KeycloakUserToUserIdentityConverter()
    )

    @Bean
    fun keycloak(properties: KeycloakProperties): Keycloak {
        return Keycloak.getInstance(
            properties.url,
            KeycloakClient.REALM,
            properties.username,
            properties.password,
            "admin-cli"
        )
    }

    @Bean
    fun customerManagement(properties: HubSpotProperties): CustomerManagementProvider =
        HubSpotClient(objectMapper = objectMapper, hubspotProperties = properties)

    @Bean
    fun metadataProvider(keycloak: Keycloak): MetadataProvider =
        KeycloakMetadataProvider(keycloak)
}