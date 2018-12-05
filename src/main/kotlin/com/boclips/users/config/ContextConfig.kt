package com.boclips.users.config

import com.boclips.users.infrastructure.keycloakclient.KeycloakClient
import com.boclips.users.infrastructure.keycloakclient.KeycloakProperties
import com.boclips.users.infrastructure.mixpanel.MixpanelClient
import com.boclips.users.infrastructure.mixpanel.MixpanelProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class ContextConfig {

    @Bean
    fun analyticsClient(properties: MixpanelProperties) = MixpanelClient(properties)

    @Bean
    fun identityProvider(properties: KeycloakProperties) = KeycloakClient(properties)
}