package com.boclips.users.config

import com.boclips.eventbus.infrastructure.SynchronousFakeEventBus
import com.boclips.users.application.CaptchaProvider
import com.boclips.users.domain.service.user.IdentityProvider
import com.boclips.users.domain.service.marketing.MarketingService
import com.boclips.users.domain.service.user.SessionProvider
import com.boclips.users.infrastructure.hubspot.resources.HubSpotProperties
import com.boclips.users.infrastructure.mixpanel.MixpanelClientFake
import com.boclips.users.infrastructure.recaptcha.GoogleRecaptchaProperties
import com.boclips.users.testsupport.KeycloakClientFake
import com.boclips.videos.api.httpclient.test.fakes.SubjectsClientFake
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("test")
@Configuration
class TestContextConfig {

    @Bean
    fun analyticsClient() = MixpanelClientFake()

    @Bean
    fun keycloakClientFake(): KeycloakClientFake = KeycloakClientFake()

    @Bean
    fun identityProvider(keycloakClientFake: KeycloakClientFake): IdentityProvider = keycloakClientFake

    @Bean
    fun sessionProvider(keycloakClientFake: KeycloakClientFake): SessionProvider = keycloakClientFake

    @Bean
    fun customerManagement(properties: HubSpotProperties): MarketingService =
        Mockito.mock(MarketingService::class.java)

    @Bean
    fun captchaProvider(properties: GoogleRecaptchaProperties): CaptchaProvider =
        Mockito.mock(CaptchaProvider::class.java)

    @Bean
    fun fakeSubjectsClient() = SubjectsClientFake()

    @Bean
    fun eventBus(): SynchronousFakeEventBus {
        return SynchronousFakeEventBus()
    }
}
