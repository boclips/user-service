package com.boclips.users.testsupport

import com.boclips.users.infrastructure.keycloakclient.KeycloakClientFake
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
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
    lateinit var keycloakClientFake: KeycloakClientFake

    @BeforeEach
    fun cleanUpDatabases() {
        keycloakClientFake.clear()
    }
}