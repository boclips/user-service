package com.boclips.users.config

import com.boclips.users.application.CaptchaProvider
import com.boclips.users.infrastructure.organisation.UserSourceResolver
import com.boclips.users.domain.service.AccountProvider
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.domain.service.SessionProvider
import com.boclips.users.infrastructure.hubspot.HubSpotClient
import com.boclips.users.infrastructure.hubspot.resources.HubSpotProperties
import com.boclips.users.infrastructure.keycloak.KeycloakProperties
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper
import com.boclips.users.infrastructure.keycloak.client.KeycloakClient
import com.boclips.users.infrastructure.keycloak.client.KeycloakUserToAccountConverter
import com.boclips.users.infrastructure.mixpanel.MixpanelClient
import com.boclips.users.infrastructure.mixpanel.MixpanelProperties
import com.boclips.users.infrastructure.recaptcha.GoogleRecaptchaClient
import com.boclips.users.infrastructure.recaptcha.GoogleRecaptchaProperties
import com.boclips.users.infrastructure.referralrock.ReferralRockClient
import com.boclips.users.infrastructure.referralrock.ReferralRockProperties
import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import com.boclips.users.infrastructure.user.UserDocumentConverter
import com.boclips.users.infrastructure.videoservice.VideoServiceProperties
import com.boclips.videos.service.client.VideoServiceClient
import com.fasterxml.jackson.databind.ObjectMapper
import org.keycloak.admin.client.Keycloak
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.client.RestTemplate

@Profile("!test")
@Configuration
class ContextConfig(
    val objectMapper: ObjectMapper
) {
    @Bean
    fun analyticsClient(properties: MixpanelProperties) = MixpanelClient(properties)

    @Bean
    fun keycloakWrapper(keycloak: Keycloak) = KeycloakWrapper(keycloak)

    @Bean
    fun keycloakClient(keycloakWrapper: KeycloakWrapper, userSourceResolver: UserSourceResolver) = KeycloakClient(
        keycloakWrapper,
        KeycloakUserToAccountConverter(userSourceResolver)
    )

    @Bean
    fun accountProvider(keycloakClient: KeycloakClient): AccountProvider = keycloakClient

    @Bean
    fun sessionProvider(keycloakClient: KeycloakClient): SessionProvider = keycloakClient

    @Bean
    fun keycloak(properties: KeycloakProperties): Keycloak {
        return Keycloak.getInstance(
            properties.url,
            KeycloakWrapper.REALM,
            properties.username,
            properties.password,
            "admin-cli"
        )
    }

    @Bean
    fun customerManagement(
        properties: HubSpotProperties,
        subjectService: VideoServiceSubjectsClient
    ): MarketingService =
        HubSpotClient(
            objectMapper = objectMapper,
            hubspotProperties = properties,
            restTemplate = RestTemplate()
        )

    @Bean
    fun referralProvider(referralRockProperties: ReferralRockProperties): ReferralProvider =
        ReferralRockClient(properties = referralRockProperties, objectMapper = objectMapper)

    @Bean
    fun captchaProvider(googleRecaptchaProperties: GoogleRecaptchaProperties): CaptchaProvider =
        GoogleRecaptchaClient(properties = googleRecaptchaProperties)

    @Bean
    fun videoServiceClient(videoServiceProperties: VideoServiceProperties) =
        VideoServiceClient.getUnauthorisedApiClient(videoServiceProperties.baseUrl)

    @Bean
    fun cacheableSubjectsClient(videoServiceClient: VideoServiceClient) = CacheableSubjectsClient(videoServiceClient)

    @Bean
    fun subjectService(cacheableSubjectsClient: CacheableSubjectsClient) =
        VideoServiceSubjectsClient(cacheableSubjectsClient)

    @Bean
    fun userDocumentConverter(subjectService: VideoServiceSubjectsClient): UserDocumentConverter {
        return UserDocumentConverter(subjectService)
    }
}
