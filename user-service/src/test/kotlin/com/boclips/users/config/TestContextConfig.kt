package com.boclips.users.config

import com.boclips.eventbus.infrastructure.SynchronousFakeEventBus
import com.boclips.users.application.CaptchaProvider
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.SessionProvider
import com.boclips.users.domain.service.SubjectService
import com.boclips.users.infrastructure.hubspot.resources.HubSpotProperties
import com.boclips.users.infrastructure.mixpanel.MixpanelClientFake
import com.boclips.users.infrastructure.recaptcha.GoogleRecaptchaProperties
import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.infrastructure.user.UserDocumentConverter
import com.boclips.users.testsupport.FakeSubjectService
import com.boclips.users.testsupport.KeycloakClientFake
import com.boclips.videos.api.httpclient.SubjectsClient
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
    fun subjectService() = FakeSubjectService()

    @Bean
    fun userDocumentConverter(subjectService: SubjectService) = UserDocumentConverter(subjectService)

    @Bean
    fun subjectsClient() = SubjectsClientFake()

    @Bean
    fun cacheableSubjectsClient(subjectsClient: SubjectsClient) = CacheableSubjectsClient(subjectsClient)

    @Bean
    fun eventBus(): SynchronousFakeEventBus {
        return SynchronousFakeEventBus()
    }
}
