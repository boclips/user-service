package com.boclips.users.client

import com.boclips.users.client.implementation.ApiUserServiceClient
import com.boclips.users.client.implementation.FakeUserServiceClient
import com.boclips.users.client.testsupport.AbstractClientIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

abstract class UserServiceClientContractTest : AbstractClientIntegrationTest() {
    @Test
    fun `returns null if it doesn't find the user`() {
        assertThat(client.findUser("this does not exist")).isNull()
    }

    @Test
    fun `returns user corresponding to provided id`() {
        val userId = "test-id"
        val organisationId = "test-organisation-id"
        insertTestUser(userId, organisationId)

        assertThat(client.findUser(userId).organisationId).isEqualTo(organisationId)
    }

    abstract fun insertTestUser(id: String, organisationId: String)

    lateinit var client: UserServiceClient
}

class ApiUserServiceClientContractTest : UserServiceClientContractTest() {
    @BeforeEach
    fun initialiseApiClient() {
        client = ApiUserServiceClient(
            RestTemplateBuilder()
                .rootUri(userServiceUrl())
                .build()
        )
    }

    override fun insertTestUser(id: String, organisationId: String) {
        mvc.perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                     "email": "jane@doe.com",
                     "password": "Champagn3",
                     "recaptchaToken": "captcha-123"
                     }
                    """.trimIndent()
                )
        )
    }
}

class FakeUserServiceClientContractTest : UserServiceClientContractTest() {
    @BeforeEach
    fun initialiseFakeClient() {
        client = FakeUserServiceClient()
    }

    override fun insertTestUser(id: String, organisationId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
