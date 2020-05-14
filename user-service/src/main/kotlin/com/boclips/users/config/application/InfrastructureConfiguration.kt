package com.boclips.users.config.application

import com.boclips.users.application.CaptchaProvider
import com.boclips.users.config.security.AppKeycloakConfigResolver
import com.boclips.users.domain.service.marketing.MarketingService
import com.boclips.users.domain.service.organisation.AmericanSchoolsProvider
import com.boclips.users.domain.service.user.IdentityProvider
import com.boclips.users.domain.service.user.SessionProvider
import com.boclips.users.infrastructure.hubspot.HubSpotClient
import com.boclips.users.infrastructure.hubspot.resources.HubSpotProperties
import com.boclips.users.infrastructure.keycloak.KeycloakProperties
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper
import com.boclips.users.infrastructure.keycloak.client.KeycloakClient
import com.boclips.users.infrastructure.keycloak.client.KeycloakUserToAccountConverter
import com.boclips.users.infrastructure.organisation.MongoOrganisationRepository
import com.boclips.users.infrastructure.recaptcha.GoogleRecaptchaClient
import com.boclips.users.infrastructure.recaptcha.GoogleRecaptchaProperties
import com.boclips.users.infrastructure.schooldigger.SchoolDiggerClient
import com.boclips.users.infrastructure.schooldigger.SchoolDiggerProperties
import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import com.boclips.users.infrastructure.user.MongoUserRepository
import com.boclips.users.infrastructure.user.UserDocumentConverter
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
    private val subjectsClient: SubjectsClient,
    private val mongoProperties: MongoProperties,
    private val keycloakProperties: KeycloakProperties,
    private val googleRecaptchaProperties: GoogleRecaptchaProperties,
    private val schoolDiggerProperties: SchoolDiggerProperties,
    private val hubspotProperties: HubSpotProperties,
    private val tracer: Tracer
) {
    @Profile("!test")
    @Bean
    fun keycloakResolver() = AppKeycloakConfigResolver(keycloakProperties)

    @Profile("!test")
    @Bean
    fun keycloakWrapper() = KeycloakWrapper(keycloak())

    @Profile("!test")
    @Bean
    fun keycloakClient() =
        KeycloakClient(
            keycloakWrapper(),
            KeycloakUserToAccountConverter()
        )

    @Profile("!test")
    @Bean
    fun identityProvider(): IdentityProvider = keycloakClient()

    @Profile("!test")
    @Bean
    fun sessionProvider(): SessionProvider = keycloakClient()

    @Profile("!test")
    @Bean
    fun keycloak(): Keycloak {
        return Keycloak.getInstance(
            keycloakProperties.url,
            KeycloakWrapper.REALM,
            keycloakProperties.username,
            keycloakProperties.password,
            "boclips-admin"
        )
    }

    @Profile("!test")
    @Bean
    fun customerManagement(): MarketingService =
        HubSpotClient(
            objectMapper = objectMapper,
            hubspotProperties = hubspotProperties,
            restTemplate = RestTemplate()
        )

    @Profile("!test")
    @Bean
    fun captchaProvider(): CaptchaProvider =
        GoogleRecaptchaClient(properties = googleRecaptchaProperties)

    @Bean
    fun cacheableSubjectsClient() = CacheableSubjectsClient(subjectsClient)

    @Bean
    fun subjectService() =
        VideoServiceSubjectsClient(cacheableSubjectsClient())

    @Bean
    fun userDocumentConverter(): UserDocumentConverter {
        return UserDocumentConverter(subjectService())
    }

    @Profile("!test")
    @Bean
    fun americanSchoolsProvider(): AmericanSchoolsProvider =
        SchoolDiggerClient(properties = schoolDiggerProperties, restTemplate = RestTemplate())

    @Bean
    fun mongoUserRepository(): MongoUserRepository {
        return MongoUserRepository(
            mongoClient(),
            userDocumentConverter()
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
