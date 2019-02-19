package com.boclips.users.testsupport

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.domain.service.MetadataProvider
import com.boclips.users.infrastructure.keycloak.client.KeycloakClientFake
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.CrudRepository
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
abstract class AbstractSpringIntergrationTest {

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

    @BeforeEach
    fun cleanUpDatabases() {
        repositories.forEach { it.deleteAll() }
        identityProvider.clear()
    }

    fun saveUser(user: User) {
        accountRepository.save(user.account)
        identityProvider.createUser(user.identity)
    }
}