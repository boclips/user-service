package com.boclips.users.testsupport

import com.boclips.eventbus.infrastructure.SynchronousFakeEventBus
import com.boclips.users.application.CaptchaProvider
import com.boclips.users.application.commands.AddCollectionToContract
import com.boclips.users.application.commands.GetOrImportUser
import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.service.AccessService
import com.boclips.users.domain.service.AccountProvider
import com.boclips.users.domain.service.ContractRepository
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.OrganisationAccountRepository
import com.boclips.users.domain.service.OrganisationService
import com.boclips.users.domain.service.SelectedContentContractRepository
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserService
import com.boclips.users.infrastructure.organisation.OrganisationIdResolver
import com.boclips.users.infrastructure.schooldigger.FakeAmericanSchoolsProvider
import com.boclips.users.presentation.hateoas.ContractLinkBuilder
import com.boclips.users.presentation.resources.ContractConverter
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.videos.service.client.spring.MockVideoServiceClient
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
@MockVideoServiceClient
abstract class AbstractSpringIntegrationTest {
    @Autowired
    protected lateinit var wireMockServer: WireMockServer

    @Autowired
    lateinit var eventBus: SynchronousFakeEventBus

    @Autowired
    protected lateinit var mvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var getOrImportUser: GetOrImportUser

    @Autowired
    lateinit var organisationAccountRepository: OrganisationAccountRepository

    @Autowired
    lateinit var keycloakClientFake: KeycloakClientFake

    @Autowired
    lateinit var accountProvider: AccountProvider

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
    lateinit var organisationService: OrganisationService

    @BeforeEach
    fun resetState() {
        repositories.forEach { it.deleteAll() }
        keycloakClientFake.clear()
        wireMockServer.resetAll()
        subjectService.reset()

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
            Account(
                id = user.id,
                username = user.account.email!!,
                roles = listOf("ROLE_TEACHER")
            )
        )

        keycloakClientFake.addUserSession(Instant.now())

        return user.id.value
    }

    fun saveOrganisationWithContractDetails(
        organisationName: String = "Boclips for Teachers",
        contractIds: List<Contract> = emptyList()
    ): OrganisationAccount<*> {
        val organisationContracts = mutableListOf<Contract>()
        contractIds.forEach {
            when (it) {
                is Contract.SelectedContent -> {
                    organisationContracts.add(
                        selectedContentContractRepository.saveSelectedContentContract(
                            it.name,
                            it.collectionIds
                        )
                    )
                }
            }
        }

        return saveApiIntegration(
            organisation = OrganisationFactory.apiIntegration(name = organisationName),
            contractIds = organisationContracts.map { it.id })
    }

    fun saveApiIntegration(
        contractIds: List<ContractId> = emptyList(),
        role: String = "ROLE_VIEWSONIC",
        organisation: ApiIntegration = OrganisationFactory.apiIntegration()
    ): OrganisationAccount<ApiIntegration> {
        return organisationAccountRepository.save(
            apiIntegration = organisation,
            contractIds = contractIds,
            role = role
        )
    }

    fun saveDistrict(
        district: District = OrganisationFactory.district()
    ): OrganisationAccount<District> {
        return organisationAccountRepository.save(
            district = district
        )
    }

    fun saveSchool(
        school: School = OrganisationFactory.school()
    ): OrganisationAccount<School> {
        return organisationAccountRepository.save(school = school)
    }

    fun saveSelectedContentContract(name: String, collectionIds: List<CollectionId>): Contract.SelectedContent {
        return selectedContentContractRepository.saveSelectedContentContract(name, collectionIds)
    }

    fun ResultActions.andExpectApiErrorPayload(): ResultActions {
        return this.andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.path").exists())
    }
}
