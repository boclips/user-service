package com.boclips.users.testsupport

import com.boclips.eventbus.infrastructure.SynchronousFakeEventBus
import com.boclips.users.application.CaptchaProvider
import com.boclips.users.application.commands.AddCollectionToAccessRule
import com.boclips.users.application.commands.GetOrImportUser
import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.ContentPackage
import com.boclips.users.domain.model.contentpackage.ContentPackageId
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.service.AccessExpiryService
import com.boclips.users.domain.service.AccessRuleRepository
import com.boclips.users.domain.service.ContentPackageRepository
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.IncludedContentAccessRuleRepository
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.domain.service.OrganisationService
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserService
import com.boclips.users.infrastructure.organisation.OrganisationIdResolver
import com.boclips.users.infrastructure.schooldigger.FakeAmericanSchoolsProvider
import com.boclips.users.presentation.hateoas.AccessRuleLinkBuilder
import com.boclips.users.presentation.hateoas.ContentPackageLinkBuilder
import com.boclips.users.presentation.resources.converters.AccessRuleConverter
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.videos.api.httpclient.test.fakes.SubjectsClientFake
import com.github.tomakehurst.wiremock.WireMockServer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.data.repository.CrudRepository
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.Instant

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
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
    lateinit var repositories: Collection<CrudRepository<*, *>>

    @Autowired
    lateinit var captchaProvider: CaptchaProvider

    @Autowired
    lateinit var marketingService: MarketingService

    @Autowired
    lateinit var subjectService: FakeSubjectService

    @Autowired
    lateinit var organisationIdResolver: OrganisationIdResolver

    @Autowired
    lateinit var includedContentAccessRuleRepository: IncludedContentAccessRuleRepository

    @Autowired
    lateinit var accessRuleRepository: AccessRuleRepository

    @Autowired
    lateinit var accessRuleConverter: AccessRuleConverter

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

    @BeforeEach
    fun resetState() {
        repositories.forEach { it.deleteAll() }
        keycloakClientFake.clear()
        wireMockServer.resetAll()
        subjectService.reset()
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

    fun saveOrganisationWithContentPackage(
        organisationName: String = "Boclips for Teachers",
        contentPackageId: ContentPackageId? = null,
        allowsOverridingUserIds: Boolean = false
    ): Organisation<*> {
        return saveApiIntegration(
            details = OrganisationDetailsFactory.apiIntegration(
                name = organisationName,
                allowsOverridingUserIds = allowsOverridingUserIds
            ),
            contentPackageId = contentPackageId
        )
    }

    fun saveContentPackage(
        contentPackage: ContentPackage
    ): ContentPackage {
        return contentPackageRepository.save(contentPackage)
    }

    fun saveApiIntegration(
        contentPackageId: ContentPackageId? = null,
        role: String = "ROLE_VIEWSONIC",
        details: ApiIntegration = OrganisationDetailsFactory.apiIntegration(allowsOverridingUserIds = false)
    ): Organisation<ApiIntegration> {
        return organisationRepository.save(
            OrganisationFactory.sample(
                details = details,
                role = role,
                contentPackageId = contentPackageId
            )
        )
    }

    fun saveDistrict(
        district: District = OrganisationDetailsFactory.district()
    ): Organisation<District> {
        return organisationRepository.save(
            OrganisationFactory.sample(
                details = district
            )
        )
    }

    fun saveSchool(
        school: School = OrganisationDetailsFactory.school()
    ): Organisation<School> {
        return organisationRepository.save(OrganisationFactory.sample(details = school))
    }

    fun saveIncludedCollectionsAccessRule(
        name: String,
        collectionIds: List<CollectionId>
    ): AccessRule.IncludedCollections {
        return includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(name, collectionIds)
    }

    fun saveIncludedVideosAccessRule(name: String, videoIds: List<VideoId>): AccessRule.IncludedVideos {
        return includedContentAccessRuleRepository.saveIncludedVideosAccessRule(name, videoIds)
    }

    fun ResultActions.andExpectApiErrorPayload(): ResultActions {
        return this.andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.path").exists())
    }
}
