package com.boclips.users.config

import com.boclips.users.domain.model.users.CustomerManagementProvider
import com.boclips.users.infrastructure.hubspot.HubSpotClient
import com.boclips.users.infrastructure.hubspot.HubSpotProperties
import com.boclips.users.infrastructure.keycloakclient.KeycloakClient
import com.boclips.users.infrastructure.keycloakclient.KeycloakProperties
import com.boclips.users.infrastructure.mixpanel.MixpanelClient
import com.boclips.users.infrastructure.mixpanel.MixpanelProperties
import com.fasterxml.jackson.databind.ObjectMapper
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
    fun identityProvider(properties: KeycloakProperties) = KeycloakClient(properties)

    @Bean
    fun customerManagement(properties: HubSpotProperties): CustomerManagementProvider =
        HubSpotClient(objectMapper = objectMapper, hubspotProperties = properties)
}