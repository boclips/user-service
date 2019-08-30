package com.boclips.users.testsupport

import com.boclips.eventbus.infrastructure.SynchronousFakeEventBus
import com.boclips.users.application.CaptchaProvider
import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.AccountProvider
import com.boclips.users.domain.service.ContractRepository
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.domain.service.SelectedContentContractRepository
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.infrastructure.organisation.UserSourceResolver
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import com.boclips.users.presentation.resources.ContractConverter
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
    lateinit var organisationRepository: OrganisationRepository

    @Autowired
    lateinit var keycloakClientFake: KeycloakClientFake

    @Autowired
    lateinit var accountProvider: AccountProvider

    @Autowired
    lateinit var repositories: Collection<CrudRepository<*, *>>

    @Autowired
    lateinit var referralProvider: ReferralProvider

    @Autowired
    lateinit var captchaProvider: CaptchaProvider

    @Autowired
    lateinit var marketingService: MarketingService

    @Autowired
    lateinit var subjectService: VideoServiceSubjectsClient

    @Autowired
    lateinit var userSourceResolver: UserSourceResolver

    @Autowired
    lateinit var selectedContentContractRepository: SelectedContentContractRepository

    @Autowired
    lateinit var contractRepository: ContractRepository

    @Autowired
    lateinit var contractConverter: ContractConverter

    @BeforeEach
    fun resetState() {
        repositories.forEach { it.deleteAll() }
        keycloakClientFake.clear()
        wireMockServer.resetAll()

        Mockito.reset(referralProvider)
        Mockito.reset(captchaProvider)
        Mockito.reset(marketingService)

        whenever(captchaProvider.validateCaptchaToken(any())).thenReturn(true)
        whenever(subjectService.allSubjectsExist(any())).thenReturn(true)
        whenever(subjectService.getSubjectsById(any())).thenReturn(
            listOf(
                Subject(
                    name = "Maths",
                    id = SubjectId(value = "1")
                )
            )
        )

        eventBus.clearState()
    }

    fun saveUser(user: User): User {
        userRepository.save(user)

        saveAccount(user)

        return user
    }

    fun saveAccount(user: User): String {
        keycloakClientFake.createAccount(
            Account(
                id = user.id,
                username = user.account.email!!,
                organisationType = user.account.organisationType
            )
        )

        keycloakClientFake.addUserSession(Instant.now())

        return user.id.value
    }

    fun saveOrganisationWithContractDetails(
        organisationName: String = "Boclips for Teachers",
        contractIds: List<Contract> = emptyList()
    ): Organisation {
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

        return saveOrganisation(organisationName, organisationContracts.map { it.id })
    }

    fun saveOrganisation(
        organisationName: String = "Boclips for Teachers",
        contractIds: List<ContractId> = emptyList()
    ): Organisation {

        return organisationRepository.save(
            organisationName = organisationName,
            contractIds = contractIds
        )
    }

    fun saveSelectedContentContract(name: String, collectionIds: List<CollectionId>): Contract.SelectedContent {
        return selectedContentContractRepository.saveSelectedContentContract(name, collectionIds)
    }
}
