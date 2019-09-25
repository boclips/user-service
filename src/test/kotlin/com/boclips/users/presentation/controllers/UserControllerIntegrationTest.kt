package com.boclips.users.presentation.controllers

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asBackofficeUser
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.UserFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
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
                     "ages": [4,5,6],
                     "country": "USA",
                     "state": "CA",
                     "schoolName": "San Fran Forest School"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.profile.href", endsWith("/users/user-id")))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.firstName", equalTo("jane")))
            .andExpect(jsonPath("$.lastName", equalTo("doe")))
            .andExpect(jsonPath("$.ages", equalTo(listOf(4, 5, 6))))
            .andExpect(jsonPath("$.subjects", hasSize<Int>(1)))
            .andExpect(jsonPath("$.country.id", equalTo("USA")))
            .andExpect(jsonPath("$.country.name", equalTo("United States")))
    }

    @Test
    fun `get own profile`() {
        val organisation = saveSchool()
        val user = saveUser(UserFactory.sample(organisationAccountId = organisation.id))

        mvc.perform(
            get("/v1/users/${user.id.value}").asUser(user.id.value)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.firstName").exists())
            .andExpect(jsonPath("$.lastName").exists())
            .andExpect(jsonPath("$.analyticsId").exists())
            .andExpect(jsonPath("$.organisation.name").exists())
            .andExpect(jsonPath("$.organisation.state").exists())
            .andExpect(jsonPath("$.organisation.country").exists())
            .andExpect(jsonPath("$._links.self.href", endsWith("/users/${user.id.value}")))
            .andExpect(jsonPath("$._links.contracts").doesNotExist())
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

    @Nested
    inner class Contracts {
        @Test
        fun `returns a link to contracts if user has VIEW_CONTRACTS role`() {
            val user = saveUser(UserFactory.sample())

            mvc.perform(
                get("/v1/users/${user.id.value}").asUserWithRoles(user.id.value, UserRoles.VIEW_CONTRACTS)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._links.contracts.href", endsWith("/v1/users/${user.id.value}/contracts")))
        }

        @Test
        fun `lists contracts user has access to`() {
            val contractName = "Test contract"
            val collectionId = "test-collection-id"
            val testContract = saveSelectedContentContract(
                name = contractName,
                collectionIds = listOf(CollectionId(collectionId))
            )

            val organisation = saveApiIntegration(
                contractIds = listOf(
                    testContract.id
                )
            )

            val user = saveUser(
                UserFactory.sample(organisationAccountId = organisation.id)
            )

            mvc.perform(
                get("/v1/users/${user.id.value}/contracts").asUserWithRoles(user.id.value, UserRoles.VIEW_CONTRACTS)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.contracts", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.contracts[0].type", equalTo("SelectedContent")))
                .andExpect(jsonPath("$._embedded.contracts[0].name", equalTo(contractName)))
                .andExpect(jsonPath("$._embedded.contracts[0].collectionIds", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.contracts[0].collectionIds[0]", equalTo(collectionId)))
                .andExpect(
                    jsonPath(
                        "$._embedded.contracts[0]._links.self.href",
                        endsWith("/v1/contracts/${testContract.id.value}")
                    )
                )
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/users/${user.id.value}/contracts")))
        }

        @Test
        fun `returns an empty list of contracts when user does not belong to an organisation`() {
            val user = saveUser(UserFactory.sample())

            mvc.perform(
                get("/v1/users/${user.id.value}/contracts").asUserWithRoles(user.id.value, UserRoles.VIEW_CONTRACTS)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.contracts", hasSize<Int>(0)))
        }

        @Test
        fun `returns a 403 response when caller does not have VIEW_CONTRACTS role`() {
            val user = saveUser(UserFactory.sample())

            mvc.perform(
                get("/v1/users/${user.id.value}/contracts").asUser(user.id.value)
            )
                .andExpect(status().isForbidden)
        }
    }
}
