package com.boclips.users.testsupport

import com.boclips.users.application.CaptchaProvider
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
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
import com.boclips.events.config.Subscriptions
import com.boclips.events.config.Topics
import org.springframework.cloud.stream.test.binder.MessageCollector

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
    protected lateinit var mvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var identityProvider: KeycloakClientFake

    @Autowired
    lateinit var repositories: Collection<CrudRepository<*, *>>

    @Autowired
    lateinit var referralProvider: ReferralProvider

    @Autowired
    lateinit var captchaProvider: CaptchaProvider

    @Autowired
    lateinit var customerManagementProvider: CustomerManagementProvider

    @Autowired
    lateinit var subjectService: VideoServiceSubjectsClient

    @Autowired
    lateinit var topics: Topics

    @Autowired
    lateinit var subscriptions: Subscriptions

    @Autowired
    lateinit var messageCollector: MessageCollector

    @BeforeEach
    fun resetState() {
        repositories.forEach { it.deleteAll() }
        identityProvider.clear()
        wireMockServer.resetAll()

        Mockito.reset(referralProvider)
        Mockito.reset(captchaProvider)
        Mockito.reset(customerManagementProvider)

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
    }

    fun saveUser(user: User): String {
        userRepository.save(user)

        identityProvider.createUser(
            Identity(
                id = user.id,
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email,
                isVerified = false
            )
        )

        return user.id.value
    }
}
