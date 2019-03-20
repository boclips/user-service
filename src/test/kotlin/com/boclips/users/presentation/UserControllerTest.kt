package com.boclips.users.presentation

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import org.hamcrest.Matchers.endsWith
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerTest : AbstractSpringIntegrationTest() {

    @Test
    fun `can create a new user with valid request`() {
        mvc.perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"firstName": "jane",
                     "lastName": "doe",
                     "subjects": "some subjects",
                     "email": "jane@doe.com",
                     "password": "Champagn3",
                     "analyticsId": "mxp-123",
                     "referralCode": "RR-123"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
    }

    @Test
    fun `can handle conflicts with valid request`() {
        val payload = """
                    {"firstName": "jane",
                     "lastName": "doe",
                     "subjects": "some subjects",
                     "email": "jane@doe.com",
                     "password": "Champagn3",
                     "analyticsId": "mxp-123",
                     "referralCode": "RR-123"
                     }
                    """.trimIndent()

        mvc.perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isCreated)

        mvc.perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `cannot create account with invalid request`() {
        mvc.perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"firstName": "jane",
                     "lastName": "doe",
                     "subjects": "some subjects",
                     "email": "jane@doe.com",
                     }
                    """.trimIndent()
                )
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    fun `activate user`() {
        mvc.perform(post("/v1/users/activate").asUser("activated-user"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.profile.href", endsWith("/users/{id}")))
    }
}