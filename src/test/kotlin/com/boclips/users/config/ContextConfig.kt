package com.boclips.users.config

import com.boclips.users.application.CaptchaProvider
import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.domain.service.SessionProvider
import com.boclips.users.infrastructure.hubspot.resources.HubSpotProperties
import com.boclips.users.infrastructure.mixpanel.MixpanelClientFake
import com.boclips.users.infrastructure.recaptcha.GoogleRecaptchaProperties
import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import com.boclips.users.infrastructure.user.UserDocumentConverter
import com.boclips.users.testsupport.KeycloakClientFake
import com.boclips.videos.service.client.VideoServiceClient
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
    fun keycloakClientFake(): KeycloakClientFake = KeycloakClientFake()

    @Bean
    fun identityProvider(keycloakClientFake: KeycloakClientFake): IdentityProvider = keycloakClientFake

    @Bean
    fun sessionProvider(keycloakClientFake: KeycloakClientFake): SessionProvider = keycloakClientFake

    @Bean
    fun customerManagement(properties: HubSpotProperties): CustomerManagementProvider =
        Mockito.mock(CustomerManagementProvider::class.java)

    @Bean
    fun referralProvider(properties: HubSpotProperties): ReferralProvider =
        Mockito.mock(ReferralProvider::class.java)

    @Bean
    fun captchaProvider(properties: GoogleRecaptchaProperties): CaptchaProvider =
        Mockito.mock(CaptchaProvider::class.java)

    @Bean
    fun subjectService() = Mockito.mock(VideoServiceSubjectsClient::class.java)

    @Bean
    fun userDocumentConverter(subjectService: VideoServiceSubjectsClient) = UserDocumentConverter(subjectService)

    @Bean
    fun cacheableSubjectsClient(videoServiceClient: VideoServiceClient) = CacheableSubjectsClient(videoServiceClient)
}