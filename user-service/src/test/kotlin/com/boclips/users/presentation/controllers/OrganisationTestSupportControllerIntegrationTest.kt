package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.deal
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder

class OrganisationTestSupportControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class CreatingOrganisations {
        @Test
        fun `returns a 403 response when user does not have an INSERT_ORGANISATIONS role`() {
            mvc.perform(
                post("/v1/api-integrations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ }")
                    .asUser("dont-have-roles@test.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `inserts an organisation`() {
            val contentPackage = saveContentPackage(ContentPackageFactory.sample())

            mvc.perform(
                post("/v1/api-integrations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "name": "Test Organisation",
                            "role": "ROLE_TEST_ORGANISATION",
                            "contentPackageId": "${contentPackage.id.value}"
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("has-role@test.com", UserRoles.INSERT_ORGANISATIONS)
            )
                .andExpect(status().isCreated)
                .andDo { result ->
                    mvc.perform(
                        get(result.response.getHeaderValue("location") as String).asUserWithRoles(
                            "has-role@test.com",
                            UserRoles.VIEW_ORGANISATIONS
                        )
                    )
                        .andExpect(status().isOk)
                        .andExpect(jsonPath("$.contentPackageId", equalTo(contentPackage.id.value)))
                }
        }

        @Test
        fun `returns a 409 response when organisation name collides`() {
            val organisationName = "Test Org"
            organisationRepository.save(
                OrganisationFactory.apiIntegration(
                    name = organisationName,
                    role = "ROLE_TEST_ORG"
                )
            )

            mvc.perform(
                post("/v1/api-integrations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "name": "$organisationName",
                            "role": "ROLE_ANOTHER",
                            "accessRuleIds": []
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("has-role@test.com", UserRoles.INSERT_ORGANISATIONS)
            )
                .andExpect(status().isConflict)
        }

        @Test
        fun `returns a 409 response when organisation role collides`() {
            val role = "ROLE_TEST_ORG"
            organisationRepository.save(
                OrganisationFactory.apiIntegration(
                    name = "Some name",
                    role = role
                )
            )

            mvc.perform(
                post("/v1/api-integrations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "name": "Some other name",
                            "role": "$role",
                            "accessRuleIds": []
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("has-role@test.com", UserRoles.INSERT_ORGANISATIONS)
            )
                .andExpect(status().isConflict)
        }

        @Test
        fun `returns a 400 response when request data is invalid`() {
            mvc.perform(
                post("/v1/api-integrations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ }")
                    .asUserWithRoles("has-role@test.com", UserRoles.INSERT_ORGANISATIONS)
            )
                .andExpect(status().isBadRequest)
                .andExpectApiErrorPayload()
        }
    }

    @Nested
    inner class FetchingOrganisationsByName {
        @Test
        fun `returns a 403 response when caller does not have a VIEW_ORGANISATIONS role`() {
            mvc.perform(
                get(
                    UriComponentsBuilder.fromUriString("/v1/api-integrations")
                        .queryParam("name", "Some org that does not exist")
                        .build()
                        .toUri()
                )
                    .asUser("has-role@test.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `returns given organisation when it's found by name`() {
            val organisationName = "Test Org"
            val organisation = organisationRepository.save(
                OrganisationFactory.apiIntegration(
                    name = organisationName,
                    role = "ROLE_TEST_ORG",
                    deal = deal(
                        contentPackageId = ContentPackageId("content-package-id")
                    )
                )
            )

            mvc.perform(
                get(
                    UriComponentsBuilder.fromUriString("/v1/api-integrations")
                        .queryParam("name", organisationName)
                        .build()
                        .toUri()
                )
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.organisationDetails.name", equalTo(organisationName)))
                .andExpect(jsonPath("$.contentPackageId", equalTo("content-package-id")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/organisations/${organisation.id.value}")))
                .andExpect(jsonPath("$._links.edit.href", endsWith("/organisations/${organisation.id.value}")))
        }

        @Test
        fun `returns a 404 response when organisation is not found by name`() {
            mvc.perform(
                get(
                    UriComponentsBuilder.fromUriString("/v1/api-integrations")
                        .queryParam("name", "Some org that does not exist")
                        .build()
                        .toUri()
                )
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `returns a 400 response when query parameter is not provided`() {
            mvc.perform(
                get("/v1/api-integrations")
                    .asUserWithRoles("viewer@hacker.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(status().isBadRequest)
        }
    }
}
