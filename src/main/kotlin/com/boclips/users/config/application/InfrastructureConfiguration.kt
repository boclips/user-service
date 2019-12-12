package com.boclips.users.config.application

import com.boclips.users.application.CaptchaProvider
import com.boclips.users.domain.service.AccountProvider
import com.boclips.users.domain.service.AmericanSchoolsProvider
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.SessionProvider
import com.boclips.users.domain.service.SubjectService
import com.boclips.users.infrastructure.hubspot.HubSpotClient
import com.boclips.users.infrastructure.hubspot.resources.HubSpotProperties
import com.boclips.users.infrastructure.keycloak.KeycloakProperties
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper
import com.boclips.users.infrastructure.keycloak.client.KeycloakClient
import com.boclips.users.infrastructure.keycloak.client.KeycloakUserToAccountConverter
import com.boclips.users.infrastructure.mixpanel.MixpanelClient
import com.boclips.users.infrastructure.mixpanel.MixpanelProperties
import com.boclips.users.infrastructure.organisation.MongoAccountRepository
import com.boclips.users.infrastructure.organisation.OrganisationIdResolver
import com.boclips.users.infrastructure.organisation.OrganisationSpringDataRepository
import com.boclips.users.infrastructure.organisation.RoleBasedOrganisationIdResolver
import com.boclips.users.infrastructure.recaptcha.GoogleRecaptchaClient
import com.boclips.users.infrastructure.recaptcha.GoogleRecaptchaProperties
import com.boclips.users.infrastructure.schooldigger.SchoolDiggerClient
import com.boclips.users.infrastructure.schooldigger.SchoolDiggerProperties
import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import com.boclips.users.infrastructure.user.MongoUserRepository
import com.boclips.users.infrastructure.user.UserDocumentConverter
import com.boclips.users.infrastructure.user.UserDocumentMongoRepository
import com.boclips.users.infrastructure.videoservice.VideoServiceProperties
import com.boclips.videos.service.client.VideoServiceClient
import com.fasterxml.jackson.databind.ObjectMapper
import org.keycloak.admin.client.Keycloak
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.client.RestTemplate

@Configuration
class InfrastructureConfiguration(
    private val objectMapper: ObjectMapper,
    private val organisationSpringDataRepository: OrganisationSpringDataRepository
) {

    @Profile("!test")
    @Bean
    fun analyticsClient(properties: MixpanelProperties) = MixpanelClient(properties)

    @Profile("!test")
    @Bean
    fun keycloakWrapper(keycloak: Keycloak) = KeycloakWrapper(keycloak)

    @Profile("!test")
    @Bean
    fun keycloakClient(keycloakWrapper: KeycloakWrapper, organisationIdResolver: OrganisationIdResolver) =
        KeycloakClient(
            keycloakWrapper,
            KeycloakUserToAccountConverter()
        )

    @Profile("!test")
    @Bean
    fun accountProvider(keycloakClient: KeycloakClient): AccountProvider = keycloakClient

    @Profile("!test")
    @Bean
    fun sessionProvider(keycloakClient: KeycloakClient): SessionProvider = keycloakClient

    @Profile("!test")
    @Bean
    fun keycloak(properties: KeycloakProperties): Keycloak {
        return Keycloak.getInstance(
            properties.url,
            KeycloakWrapper.REALM,
            properties.username,
            properties.password,
            "boclips-admin"
        )
    }

    @Profile("!test")
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

    @Profile("!test")
    @Bean
    fun captchaProvider(googleRecaptchaProperties: GoogleRecaptchaProperties): CaptchaProvider =
        GoogleRecaptchaClient(properties = googleRecaptchaProperties)

    @Profile("!test")
    @Bean
    fun videoServiceClient(videoServiceProperties: VideoServiceProperties) =
        VideoServiceClient.getUnauthorisedApiClient(videoServiceProperties.baseUrl)

    @Profile("!test")
    @Bean
    fun cacheableSubjectsClient(videoServiceClient: VideoServiceClient) = CacheableSubjectsClient(videoServiceClient)

    @Profile("!test")
    @Bean
    fun subjectService(cacheableSubjectsClient: CacheableSubjectsClient) =
        VideoServiceSubjectsClient(cacheableSubjectsClient)

    @Profile("!test")
    @Bean
    fun userDocumentConverter(subjectService: SubjectService): UserDocumentConverter {
        return UserDocumentConverter(subjectService)
    }

    @Profile("!test")
    @Bean
    fun americanSchoolsProvider(schoolDiggerProperties: SchoolDiggerProperties): AmericanSchoolsProvider =
        SchoolDiggerClient(properties = schoolDiggerProperties, restTemplate = RestTemplate())



    @Bean
    fun roleBasedOrganisationIdResolver(): RoleBasedOrganisationIdResolver {
        return RoleBasedOrganisationIdResolver(mongoOrganisationAccountRepository())
    }

    @Bean
    fun mongoUserRepository(
        userDocumentMongoRepository: UserDocumentMongoRepository,
        userDocumentConverter: UserDocumentConverter
    ): MongoUserRepository {
        return MongoUserRepository(
            userDocumentMongoRepository,
            userDocumentConverter,
            roleBasedOrganisationIdResolver()
        )
    }

    @Bean
    fun mongoOrganisationAccountRepository(

    ): MongoAccountRepository {
        return MongoAccountRepository(
            repository = organisationSpringDataRepository
        )
    }
}
