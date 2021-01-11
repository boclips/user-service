package com.boclips.users.api.request.user

import com.boclips.users.api.factories.CreateUserRequestFactory
import com.boclips.users.api.httpclient.helper.ObjectMapperDefinition
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class CreateUserRequestTest {

    private val mapper = ObjectMapperDefinition.default()

    @Test
    fun `will encode the type for CreateApiUserRequest`() {
        val request = CreateUserRequest.CreateApiUserRequest(organisationId = "abc", apiUserId = "id")

        Assertions.assertThat(mapper.writeValueAsString(request)).contains("\"type\":\"apiUser\"")
    }

    @Test
    fun `will encode the type for CreateTeacerRequest`() {
        val request = CreateUserRequestFactory.teacher()

        Assertions.assertThat(mapper.writeValueAsString(request)).contains("\"type\":\"teacher\"")
    }
}
