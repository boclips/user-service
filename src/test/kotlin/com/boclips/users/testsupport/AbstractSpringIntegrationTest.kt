package com.boclips.users.testsupport

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.domain.service.MetadataProvider
import com.boclips.users.domain.service.ReferralProvider
import com.github.tomakehurst.wiremock.WireMockServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.data.repository.CrudRepository
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 9999)
abstract class AbstractSpringIntegrationTest {
    @Autowired
    protected lateinit var wireMockServer: WireMockServer

    @Autowired
    protected lateinit var mvc: MockMvc

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var identityProvider: KeycloakClientFake

    @Autowired
    lateinit var metadataProvider: MetadataProvider

    @Autowired
    lateinit var repositories: Collection<CrudRepository<*, *>>

    @Autowired
    lateinit var referralProvider: ReferralProvider

    @BeforeEach
    fun resetState() {
        repositories.forEach { it.deleteAll() }
        identityProvider.clear()
        wireMockServer.resetAll()

        Mockito.reset(referralProvider)
    }

    fun saveUser(user: User) {
        accountRepository.save(user.account)
        identityProvider.createUser(user.identity)
    }
}