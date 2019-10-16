package com.boclips.users.presentation.controllers

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asBackofficeUser
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
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
            .andExpect(status().isBadRequest)
            .andExpectApiErrorPayload()
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
            .andExpect(status().isBadRequest)
    }

    @Nested
    inner class UpdateUser {
        @Test
        fun `returns a 403 response if caller tries to update a different user`() {
            saveUser(UserFactory.sample(account = AccountFactory.sample(id = "user-id")))

            mvc.perform(
                put("/v1/users/user-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                             "firstName": "change this",
                             "lastName": "and that"
                        }
                        """.trimIndent()
                    ).asUser("different-users-id")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `updating without a payload is a bad request`() {
            saveUser(UserFactory.sample())

            setSecurityContext("user-id")

            mvc.perform(put("/v1/users/user-id").asUser("user-id"))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `updates a user`() {
            whenever(subjectService.getSubjectsById(any())).thenReturn(
                listOf(
                    Subject(
                        name = "Maths",
                        id = SubjectId(value = "1")
                    )
                )
            )
            saveUser(UserFactory.sample())
            val school = saveSchool(
                school = OrganisationFactory.school(
                    name = "San Fran Forest School",
                    state = State.fromCode("CA"),
                    country = Country.fromCode("USA")
                )
            )

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
                .andExpect(jsonPath("$.organisationAccountId", equalTo(school.id.value)))
                .andExpect(jsonPath("$.organisation.name", equalTo("San Fran Forest School")))
                .andExpect(jsonPath("$.organisation.state.name", equalTo("California")))
                .andExpect(jsonPath("$.organisation.state.id", equalTo("CA")))
                .andExpect(jsonPath("$.organisation.country.name", equalTo("United States")))
                .andExpect(jsonPath("$.organisation.country.id", equalTo("USA")))
        }
    }

    @Nested
    inner class GetUser {
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
        fun `imports the user into the system`() {
            val userId = "709f86bf-3292-4c96-9c84-5c89a255a07c"
            val authority = "TEST_ORGANISATION"
            val organisationMatchingRole = "ROLE_$authority"
            keycloakClientFake.createAccount(
                Account(
                    id = UserId(userId),
                    username = "service-account@somewhere.com",
                    roles = listOf(organisationMatchingRole)
                )
            )
            val organisation = saveApiIntegration(
                role = organisationMatchingRole
            )

            mvc.perform(
                get("/v1/users/$userId/contracts").asUserWithRoles(userId, UserRoles.VIEW_CONTRACTS, authority)
            )
                .andExpect(status().isOk)

            val importedUser = userRepository.findById(UserId(userId))

            assertThat(importedUser).isNotNull
            assertThat(importedUser!!.organisationAccountId).isEqualTo(organisation.id)
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
