package com.boclips.users.config.application

import com.boclips.users.application.CaptchaProvider
import com.boclips.users.domain.service.AmericanSchoolsProvider
import com.boclips.users.domain.service.IdentityProvider
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
import com.boclips.users.infrastructure.organisation.MongoOrganisationRepository
import com.boclips.users.infrastructure.organisation.OrganisationResolver
import com.boclips.users.infrastructure.organisation.RoleBasedOrganisationResolver
import com.boclips.users.infrastructure.recaptcha.GoogleRecaptchaClient
import com.boclips.users.infrastructure.recaptcha.GoogleRecaptchaProperties
import com.boclips.users.infrastructure.schooldigger.SchoolDiggerClient
import com.boclips.users.infrastructure.schooldigger.SchoolDiggerProperties
import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import com.boclips.users.infrastructure.user.MongoUserRepository
import com.boclips.users.infrastructure.user.UserDocumentConverter
import com.boclips.users.infrastructure.videoservice.VideoServiceProperties
import com.boclips.videos.api.httpclient.SubjectsClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoClientURI
import io.opentracing.Tracer
import io.opentracing.contrib.mongo.common.TracingCommandListener
import org.keycloak.admin.client.Keycloak
import org.litote.kmongo.KMongo
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.client.RestTemplate

@Configuration
class InfrastructureConfiguration(
    private val objectMapper: ObjectMapper,
    val mongoProperties: MongoProperties,
    val tracer: Tracer
) {

    @Profile("!test")
    @Bean
    fun analyticsClient(properties: MixpanelProperties) = MixpanelClient(properties)

    @Profile("!test")
    @Bean
    fun keycloakWrapper(keycloak: Keycloak) = KeycloakWrapper(keycloak)

    @Profile("!test")
    @Bean
    fun keycloakClient(keycloakWrapper: KeycloakWrapper, organisationResolver: OrganisationResolver) =
        KeycloakClient(
            keycloakWrapper,
            KeycloakUserToAccountConverter()
        )

    @Profile("!test")
    @Bean
    fun identityProvider(keycloakClient: KeycloakClient): IdentityProvider = keycloakClient

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
        SubjectsClient.create(apiUrl = videoServiceProperties.baseUrl)

    @Profile("!test")
    @Bean
    fun cacheableSubjectsClient(subjectsClient: SubjectsClient) = CacheableSubjectsClient(subjectsClient)

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
    fun roleBasedOrganisationIdResolver(): RoleBasedOrganisationResolver {
        return RoleBasedOrganisationResolver(mongoOrganisationAccountRepository())
    }

    @Bean
    fun mongoUserRepository(
        userDocumentConverter: UserDocumentConverter
    ): MongoUserRepository {
        return MongoUserRepository(
            mongoClient(),
            userDocumentConverter
        )
    }

    @Bean
    fun mongoOrganisationAccountRepository(): MongoOrganisationRepository {
        return MongoOrganisationRepository(mongoClient())
    }

    @Bean
    fun mongoClient(): MongoClient {
        return KMongo.createClient(
            MongoClientURI(
                mongoProperties.determineUri(),
                MongoClientOptions.builder()
                    .maxWaitTime(10_000)
                    .socketTimeout(10_000)
                    .addCommandListener(TracingCommandListener.Builder(tracer).build())
            )
        )
    }
}
