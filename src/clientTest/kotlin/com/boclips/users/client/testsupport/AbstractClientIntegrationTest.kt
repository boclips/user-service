package com.boclips.users.client.testsupport

import com.boclips.users.config.TestContextConfig
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Import

@Import(TestContextConfig::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractClientIntegrationTest : AbstractSpringIntegrationTest() {
    @LocalServerPort
    var serverPort: Int = -1

    fun userServiceUrl() = "http://localhost:$serverPort"
}
