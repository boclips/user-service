package com.boclips.users.testsupport

import com.boclips.eventbus.infrastructure.SynchronousFakeEventBus
import com.boclips.users.application.CaptchaProvider
import com.boclips.users.application.commands.GetOrImportUser
import com.boclips.users.domain.model.access.ContentPackage
import com.boclips.users.domain.model.access.ContentPackageRepository
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.subject.Subject
import com.boclips.users.domain.model.subject.SubjectId
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.service.access.AccessExpiryService
import com.boclips.users.domain.service.marketing.MarketingService
import com.boclips.users.domain.service.organisation.OrganisationService
import com.boclips.users.domain.service.organisation.resolvers.OrganisationResolver
import com.boclips.users.domain.service.subject.SubjectService
import com.boclips.users.domain.service.user.IdentityProvider
import com.boclips.users.domain.service.user.UserCreationService
import com.boclips.users.infrastructure.MongoDatabase
import com.boclips.users.infrastructure.account.AccountDocument
import com.boclips.users.infrastructure.schooldigger.FakeAmericanSchoolsProvider
import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.presentation.hateoas.ContentPackageLinkBuilder
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.videos.api.httpclient.test.fakes.SubjectsClientFake
import com.boclips.videos.api.request.subject.CreateSubjectRequest
import com.github.tomakehurst.wiremock.WireMockServer
import com.mongodb.MongoClient
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import de.flapdoodle.embed.mongo.MongodProcess
import mu.KLogging
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.Instant

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 9999)
abstract class AbstractSpringIntegrationTest {
    @Autowired
    protected lateinit var wireMockServer: WireMockServer

    @Autowired
    lateinit var eventBus: SynchronousFakeEventBus

    @Autowired
    lateinit var subjectsClient: SubjectsClientFake

    @Autowired
    protected lateinit var mvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var getOrImportUser: GetOrImportUser

    @Autowired
    lateinit var organisationRepository: OrganisationRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var contentPackageRepository: ContentPackageRepository

    @Autowired
    lateinit var keycloakClientFake: KeycloakClientFake

    @Autowired
    lateinit var identityProvider: IdentityProvider

    @Autowired
    lateinit var accessExpiryService: AccessExpiryService

    @Autowired
    lateinit var mongoClient: MongoClient

    @Autowired
    lateinit var captchaProvider: CaptchaProvider

    @Autowired
    lateinit var marketingService: MarketingService

    @Autowired
    lateinit var subjectService: SubjectService

    @Autowired
    lateinit var organisationResolver: OrganisationResolver

    @Autowired
    lateinit var fakeAmericanSchoolsProvider: FakeAmericanSchoolsProvider

    @Autowired
    lateinit var contentPackageLinkBuilder: ContentPackageLinkBuilder

    @Autowired
    lateinit var userCreationService: UserCreationService

    @Autowired
    lateinit var organisationService: OrganisationService

    @Autowired
    lateinit var cacheableSubjectsClient: CacheableSubjectsClient

    @LocalServerPort
    var randomServerPort: Int = 0

    companion object : KLogging() {
        private var mongoProcess: MongodProcess? = null

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            if (mongoProcess == null) {
                mongoProcess = TestMongoProcess.process
            }
        }
    }

    @BeforeEach
    fun resetState() {
        mongoClient.getDatabase(MongoDatabase.DB_NAME).drop()
        keycloakClientFake.clear()
        wireMockServer.resetAll()
        cacheableSubjectsClient.flushSubjectsCache()
        subjectsClient.clear()

        Mockito.reset(captchaProvider)
        Mockito.reset(marketingService)

        whenever(captchaProvider.validateCaptchaToken(any())).thenReturn(true)

        eventBus.clearState()
        fakeAmericanSchoolsProvider.clear()

        SecurityContextHolder
            .setContext(SecurityContextImpl(null))
    }

    fun saveUser(user: User): User {
        val createdUser = userRepository.create(user)

        saveIdentityProviderAccount(createdUser)

        return createdUser
    }

    fun saveAccount(name: String): Account {
        return accountRepository.create(AccountDocument(name = name, _id = ObjectId()))
    }

    fun saveIdentityProviderAccount(user: User): String {
        keycloakClientFake.createIdentityProviderAccount(
            IdentityFactory.sample(
                id = user.id.value,
                username = user.identity.email ?: user.identity.username,
                roles = listOf("ROLE_TEACHER"),
                createdAt = user.identity.createdAt
            )
        )

        keycloakClientFake.addUserSession(Instant.now())

        return user.id.value
    }

    fun saveContentPackage(
        contentPackage: ContentPackage
    ): ContentPackage {
        return contentPackageRepository.save(contentPackage)
    }

    fun <T : Organisation> saveOrganisation(organisation: T): T {
        return organisationRepository.save(organisation)
    }

    fun saveSubject(name: String): Subject {
        subjectsClient.create(CreateSubjectRequest(name))
        return subjectsClient.findAll().find { it.name == name }!!
            .let { resource ->
                Subject(
                    id = SubjectId(
                        resource.id
                    ), name = resource.name!!
                )
            }
    }

    fun ResultActions.andExpectApiErrorPayload(): ResultActions {
        return this.andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.path").exists())
    }
}
