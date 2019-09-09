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

    @Autowired(required = false)
    lateinit var oauth2CredentialProperties: Oauth2CredentialProperties

    @Test
    fun `user service client is configured`() {
        assertThat(userServiceClient).isNotNull
    }

    @Test
    fun `configuration properties are sourced`() {
        assertThat(userServiceClientProperties).isNotNull
        assertThat(userServiceClientProperties.baseUrl).isEqualTo("https://api-gateway")
        assertThat(oauth2CredentialProperties.tokenUrl).isEqualTo("https://api-gateway/v1/token")
        assertThat(oauth2CredentialProperties.clientId).isEqualTo("user-service-client-id")
        assertThat(oauth2CredentialProperties.clientSecret).isEqualTo("user-service-client-secret")
    }

    @Nested
    inner class MissingConfigurationTest {
        @Test
        fun `throws an error if user service URL is missing`() {
            assertThatThrownBy {
                UserServiceClientConfig()
                    .userServiceClient(
                        UserServiceClientProperties().apply {
                            baseUrl = ""
                        },
                        Oauth2CredentialProperties().apply {
                            tokenUrl = "not empty"
                            clientId = "not empty"
                            clientSecret = "not empty"
                        }
                    )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `throws an error if token url is missing`() {
            assertThatThrownBy {
                UserServiceClientConfig()
                    .userServiceClient(
                        UserServiceClientProperties().apply {
                            baseUrl = "not empty"
                        },
                        Oauth2CredentialProperties().apply {
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
                            baseUrl = "not empty"
                        },
                        Oauth2CredentialProperties().apply {
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
                            baseUrl = "not empty"
                        },
                        Oauth2CredentialProperties().apply {
                            tokenUrl = "not empty"
                            clientId = "not empty"
                            clientSecret = ""
                        }
                    )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }
    }
}
