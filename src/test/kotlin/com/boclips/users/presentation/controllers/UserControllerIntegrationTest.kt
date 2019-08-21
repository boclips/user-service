package com.boclips.users.presentation.controllers

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asBackofficeUser
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.UserFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `can create a new user with valid request`() {
        mvc.perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                     "email": "jane@doe.com",
                     "password": "Champagn3",
                     "analyticsId": "mxp-123",
                     "referralCode": "RR-123",
                     "recaptchaToken": "captcha-123"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
    }

    @Test
    fun `can create a new user without optional fields`() {
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
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
    }

    @Test
    fun `can handle conflicts with valid request`() {
        val payload = """
                    {
                     "email": "jane@doe.com",
                     "password": "Champagn3",
                     "analyticsId": "mxp-123",
                     "referralCode": "RR-123",
                     "recaptchaToken": "captcha-123"
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
                    {
                     "email": "jane@doe.com"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(status().is4xxClientError)
            .andExpect(jsonPath("$.errors", hasSize<Any>(4)))
            .andExpect(jsonPath("$.errors[0].field").exists())
            .andExpect(jsonPath("$.errors[0].message").exists())
    }

    @Test
    fun `cannot create account as a robot`() {
        whenever(captchaProvider.validateCaptchaToken(any())).thenReturn(false)

        mvc.perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                     "email": "jane@doe.com",
                     "password": "Champagn3",
                     "analyticsId": "mxp-123",
                     "referralCode": "RR-123",
                     "recaptchaToken": "captcha-123"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    fun `update a user without a payload`() {
        saveUser(UserFactory.sample())

        setSecurityContext("user-id")

        mvc.perform(put("/v1/users/user-id").asUser("user-id"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `updates a user`() {
        saveUser(UserFactory.sample())

        setSecurityContext("user-id")

        mvc.perform(
            put("/v1/users/user-id").asUser("user-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"firstName": "jane",
                     "lastName": "doe",
                     "subjects": ["Maths"],
                     "hasOptedIntoMarketing": true,
                     "ages": [4,5,6],
                     "referralCode": "1234"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.profile.href", endsWith("/users/user-id")))

        val user = userRepository.findById(UserId("user-id"))!!

        assertThat(user.profile!!.firstName).isEqualTo("jane")
        assertThat(user.profile!!.lastName).isEqualTo("doe")
        assertThat(user.profile!!.hasOptedIntoMarketing).isTrue()
        assertThat(user.profile!!.ages).containsExactly(4, 5, 6)
        assertThat(user.profile!!.subjects).hasSize(1)
        assertThat(user.referralCode).isEqualTo("1234")
    }

    @Test
    fun `get own profile`() {
        val user = saveUser(UserFactory.sample())

        mvc.perform(
            get("/v1/users/${user.id.value}").asUser(user.id.value)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.firstName").exists())
            .andExpect(jsonPath("$.lastName").exists())
            .andExpect(jsonPath("$.analyticsId").exists())
            .andExpect(jsonPath("$._links.self.href", endsWith("/users/${user.id.value}")))
    }

    @Test
    fun `get user that does not exist`() {
        mvc.perform(
            get("/v1/users/rafal").asUserWithRoles("ben", UserRoles.VIEW_USERS)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `synchronise identities`() {
        mvc.perform(post("/v1/users/sync-identities").asBackofficeUser())
            .andExpect(status().isOk)
    }

    @Test
    fun `synchronise crm profiles`() {
        mvc.perform(post("/v1/users/sync").asBackofficeUser())
            .andExpect(status().isOk)
    }
}
