package com.boclips.users.testsupport

import com.boclips.eventbus.infrastructure.SynchronousFakeEventBus
import com.boclips.users.application.CaptchaProvider
import com.boclips.users.application.commands.AddCollectionToAccessRule
import com.boclips.users.application.commands.GetOrImportUser
import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.ContentPackage
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.AccessExpiryService
import com.boclips.users.domain.service.AccessRuleRepository
import com.boclips.users.domain.service.ContentPackageRepository
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.domain.service.OrganisationService
import com.boclips.users.domain.service.SubjectService
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserService
import com.boclips.users.infrastructure.MongoDatabase
import com.boclips.users.infrastructure.organisation.OrganisationResolver
import com.boclips.users.infrastructure.schooldigger.FakeAmericanSchoolsProvider
import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.presentation.hateoas.AccessRuleLinkBuilder
import com.boclips.users.presentation.hateoas.ContentPackageLinkBuilder
import com.boclips.videos.api.httpclient.test.fakes.SubjectsClientFake
import com.boclips.videos.api.request.subject.CreateSubjectRequest
import com.github.tomakehurst.wiremock.WireMockServer
import com.mongodb.MongoClient
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import de.flapdoodle.embed.mongo.MongodProcess
import mu.KLogging
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
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
    lateinit var accessRuleRepository: AccessRuleRepository

    @Autowired
    lateinit var fakeAmericanSchoolsProvider: FakeAmericanSchoolsProvider

    @Autowired
    lateinit var accessRuleLinkBuilder: AccessRuleLinkBuilder

    @Autowired
    lateinit var contentPackageLinkBuilder: ContentPackageLinkBuilder

    @Autowired
    lateinit var addCollectionToAccessRule: AddCollectionToAccessRule

    @Autowired
    lateinit var userService: UserService

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
    }

    fun saveUser(user: User): User {
        val createdUser = userRepository.create(user)

        saveAccount(createdUser)

        return createdUser
    }

    fun saveAccount(user: User): String {
        keycloakClientFake.createAccount(
            Identity(
                id = user.id,
                username = user.identity.email!!,
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

    fun saveIncludedVideosAccessRule(name: String, videoIds: List<VideoId>): AccessRule.IncludedVideos {
        return AccessRule.IncludedVideos(id = AccessRuleId(), name = name, videoIds = videoIds)
            .let(accessRuleRepository::save)
    }

    fun saveSubject(name: String): Subject {
        subjectsClient.create(CreateSubjectRequest(name))
        return subjectsClient.findAll().find { it.name == name }!!
            .let { resource -> Subject(id = SubjectId(resource.id), name = resource.name!!) }
    }

    fun ResultActions.andExpectApiErrorPayload(): ResultActions {
        return this.andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.path").exists())
    }
}
