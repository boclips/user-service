package com.boclips.users.config

import com.boclips.users.infrastructure.keycloakclient.KeycloakClientFake
import com.boclips.users.infrastructure.mixpanel.MixpanelClientFake
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("test")
@Configuration
class ContextConfig {

    @Bean
    fun analyticsClient() = MixpanelClientFake()

    @Bean
    fun identityProvider() = KeycloakClientFake()
}