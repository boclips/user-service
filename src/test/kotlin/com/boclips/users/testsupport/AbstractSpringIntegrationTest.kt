package com.boclips.users.testsupport

import com.boclips.events.config.Topics
import com.boclips.users.application.CaptchaProvider
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.domain.service.IdentityProvider
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
import org.springframework.cloud.stream.test.binder.MessageCollector
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
    protected lateinit var mvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var keycloakClientFake: KeycloakClientFake

    @Autowired
    lateinit var identityProvider: IdentityProvider

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
    lateinit var messageCollector: MessageCollector

    @BeforeEach
    fun resetState() {
        repositories.forEach { it.deleteAll() }
        keycloakClientFake.clear()
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

        messageCollector.forChannel(topics.userActivated()).clear()
    }

    fun saveUser(user: User): String {
        userRepository.save(user)

        keycloakClientFake.createUser(
            Identity(
                id = user.id,
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email,
                isVerified = false
            )
        )

        keycloakClientFake.addUserSession(Instant.now())

        return user.id.value
    }
}
