package com.boclips.users.testsupport

import com.boclips.eventbus.infrastructure.SynchronousFakeEventBus
import com.boclips.users.application.CaptchaProvider
import com.boclips.users.application.commands.AddCollectionToContract
import com.boclips.users.application.commands.GetOrImportUser
import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.account.ApiIntegration
import com.boclips.users.domain.model.account.District
import com.boclips.users.domain.model.account.School
import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.contract.VideoId
import com.boclips.users.domain.service.AccessService
import com.boclips.users.domain.service.AccountRepository
import com.boclips.users.domain.service.AccountService
import com.boclips.users.domain.service.ContractRepository
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.SelectedContentContractRepository
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserService
import com.boclips.users.infrastructure.organisation.OrganisationIdResolver
import com.boclips.users.infrastructure.schooldigger.FakeAmericanSchoolsProvider
import com.boclips.users.presentation.hateoas.ContractLinkBuilder
import com.boclips.users.presentation.resources.converters.ContractConverter
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
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var keycloakClientFake: KeycloakClientFake

    @Autowired
    lateinit var identityProvider: IdentityProvider

    @Autowired
    lateinit var accessService: AccessService

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
    lateinit var selectedContentContractRepository: SelectedContentContractRepository

    @Autowired
    lateinit var contractRepository: ContractRepository

    @Autowired
    lateinit var contractConverter: ContractConverter

    @Autowired
    lateinit var fakeAmericanSchoolsProvider: FakeAmericanSchoolsProvider

    @Autowired
    lateinit var contractLinkBuilder: ContractLinkBuilder

    @Autowired
    lateinit var addCollectionToContract: AddCollectionToContract

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var accountService: AccountService

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

    fun saveOrganisationWithContractDetails(
        organisationName: String = "Boclips for Teachers",
        contractIds: List<Contract> = emptyList()
    ): com.boclips.users.domain.model.account.Account<*> {
        val organisationContracts = mutableListOf<Contract>()
        contractIds.map {
            when (it) {
                is Contract.SelectedCollections -> {
                    organisationContracts.add(
                        selectedContentContractRepository.saveSelectedCollectionsContract(
                            it.name,
                            it.collectionIds
                        )
                    )
                }
                is Contract.SelectedVideos -> {
                    organisationContracts.add(
                        selectedContentContractRepository.saveSelectedVideosContract(
                            it.name,
                            it.videoIds
                        )
                    )
                }
            }
        }

        return saveApiIntegration(
            organisation = OrganisationFactory.apiIntegration(name = organisationName, allowsOverridingUserIds = false),
            contractIds = organisationContracts.map { it.id })
    }

    fun saveApiIntegration(
        contractIds: List<ContractId> = emptyList(),
        role: String = "ROLE_VIEWSONIC",
        organisation: ApiIntegration = OrganisationFactory.apiIntegration(allowsOverridingUserIds = false)
    ): com.boclips.users.domain.model.account.Account<ApiIntegration> {
        return accountRepository.save(
            apiIntegration = organisation,
            contractIds = contractIds,
            role = role
        )
    }

    fun saveDistrict(
        district: District = OrganisationFactory.district()
    ): com.boclips.users.domain.model.account.Account<District> {
        return accountRepository.save(
            district = district
        )
    }

    fun saveSchool(
        school: School = OrganisationFactory.school()
    ): com.boclips.users.domain.model.account.Account<School> {
        return accountRepository.save(school = school)
    }

    fun saveSelectedCollectionsContract(name: String, collectionIds: List<CollectionId>): Contract.SelectedCollections {
        return selectedContentContractRepository.saveSelectedCollectionsContract(name, collectionIds)
    }

    fun saveSelectedVideosContract(name: String, videoIds: List<VideoId>): Contract.SelectedVideos {
        return selectedContentContractRepository.saveSelectedVideosContract(name, videoIds)
    }

    fun ResultActions.andExpectApiErrorPayload(): ResultActions {
        return this.andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.path").exists())
    }
}
