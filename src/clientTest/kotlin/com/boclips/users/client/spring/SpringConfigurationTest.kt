package com.boclips.users.client.spring

import com.boclips.users.client.UserServiceClient
import com.boclips.users.client.testsupport.AbstractClientIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SpringConfigurationTest : AbstractClientIntegrationTest() {
    @Autowired(required = false)
    lateinit var userServiceClient: UserServiceClient

    @Autowired(required = false)
    lateinit var userServiceClientProperties: UserServiceClientProperties

    @Test
    fun `user service client is configured`() {
        assertThat(userServiceClient).isNotNull
    }

    @Test
    fun `configuration properties are sourced`() {
        assertThat(userServiceClientProperties).isNotNull
        assertThat(userServiceClientProperties.apiGatewayUrl).isEqualTo("https://api-gateway/v1")
        assertThat(userServiceClientProperties.tokenUrl).isEqualTo("https://api-gateway/v1/token")
        assertThat(userServiceClientProperties.clientId).isEqualTo("user-service-client-id")
        assertThat(userServiceClientProperties.clientSecret).isEqualTo("user-service-client-secret")
    }

    @Nested
    inner class MissingConfigurationTest {
        @Test
        fun `throws an error api gateway url is missing`() {
            assertThatThrownBy {
                UserServiceClientConfig()
                    .userServiceClient(
                        UserServiceClientProperties().apply {
                            apiGatewayUrl = ""
                            tokenUrl = "not empty"
                            clientId = "not empty"
                            clientSecret = "not empty"
                        }
                    )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `throws an error token url is missing`() {
            assertThatThrownBy {
                UserServiceClientConfig()
                    .userServiceClient(
                        UserServiceClientProperties().apply {
                            apiGatewayUrl = "not empty"
                            tokenUrl = ""
                            clientId = "not empty"
                            clientSecret = "not empty"
                        }
                    )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `throws an error client id is missing`() {
            assertThatThrownBy {
                UserServiceClientConfig()
                    .userServiceClient(
                        UserServiceClientProperties().apply {
                            apiGatewayUrl = "not empty"
                            tokenUrl = "not empty"
                            clientId = ""
                            clientSecret = "not empty"
                        }
                    )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `throws an error when client secret is missing`() {
            assertThatThrownBy {
                UserServiceClientConfig()
                    .userServiceClient(
                        UserServiceClientProperties().apply {
                            apiGatewayUrl = "not empty"
                            tokenUrl = "not empty"
                            clientId = "not empty"
                            clientSecret = ""
                        }
                    )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }
    }
}
