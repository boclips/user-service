package com.boclips.users.config

import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.infrastructure.hubspot.HubSpotProperties
import com.boclips.users.infrastructure.mixpanel.MixpanelClientFake
import com.boclips.users.testsupport.KeycloakClientFake
import org.mockito.Mockito
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

    @Bean
    fun customerManagement(properties: HubSpotProperties): CustomerManagementProvider =
        Mockito.mock(CustomerManagementProvider::class.java)

    @Bean
    fun referralProvider(properties: HubSpotProperties): ReferralProvider =
        Mockito.mock(ReferralProvider::class.java)
}