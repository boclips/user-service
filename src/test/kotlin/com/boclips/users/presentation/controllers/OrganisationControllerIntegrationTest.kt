package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class OrganisationControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class FetchingIndependentOrganisations {
        @Test
        fun `returns a forbidden response when caller is not allowed to view organisations`() {
            mvc.perform(
                get("/v1/organisations?countryCode=USA")
                    .asUser("has-role@test.com")
            )
                .andExpect(MockMvcResultMatchers.status().isForbidden)
        }

        @Test
        fun `lists all independent US schools and organisations`() {
            val expiryTime = ZonedDateTime.parse("2019-12-04T15:11:59.531Z")

            val district = organisationRepository.save(
                OrganisationDetailsFactory.district(
                    name = "my district",
                    externalId = "123",
                    state = State(id = "FL", name = "Florida")
                ),
                accessExpiresOn = expiryTime
            )

            organisationRepository.save(
                school = OrganisationDetailsFactory.school(
                    name = "my district school",
                    countryName = "USA",
                    state = State(id = "FL", name = "Florida"),
                    district = district
                )
            )

            organisationRepository.save(
                school = OrganisationDetailsFactory.school(
                    name = "my independent school",
                    countryName = "USA",
                    state = State(id = "FL", name = "Florida"),
                    district = null
                ),
                accessExpiresOn = expiryTime
            )

            mvc.perform(
                get("/v1/organisations?countryCode=USA&page=0&size=1").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.VIEW_ORGANISATIONS
                )
            )
                .andExpect(jsonPath("$._embedded.organisations", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.organisations[0].organisationDetails.name").exists())
                .andExpect(jsonPath("$._embedded.organisations[0].organisationDetails.type").exists())
                .andExpect(jsonPath("$._embedded.organisations[0].accessExpiresOn").exists())
                .andExpect(jsonPath("$._embedded.organisations", hasSize<Int>(1)))
                .andExpect(
                    jsonPath(
                        "$._embedded.organisations[0]._links.edit.href",
                        endsWith("/v1/organisations/${district.id.value}")
                    )
                )
                .andExpect(jsonPath("$.page.totalElements", equalTo(2)))
                .andExpect(jsonPath("$.page.totalPages", equalTo(2)))
                .andExpect(jsonPath("$.page.size", equalTo(1)))
        }

        @Test
        fun `fetches all independent organisations when no countryCode is provided`() {
            val district = organisationRepository.save(
                OrganisationDetailsFactory.district(
                    name = "my district",
                    externalId = "123",
                    state = State(id = "FL", name = "Florida")
                )
            )
            val school = organisationRepository.save(
                OrganisationDetailsFactory.school(
                    name = "my school",
                    country = Country.fromCode("GBR")
                )
            )

            mvc.perform(
                get("/v1/organisations").asUserWithRoles("some-boclipper", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(jsonPath("$._embedded.organisations", hasSize<Int>(2)))
                .andExpect(jsonPath("$._embedded.organisations[0].organisationDetails.name", equalTo(district.organisation.name)))
                .andExpect(jsonPath("$._embedded.organisations[1].organisationDetails.name", equalTo(school.organisation.name)))
        }
    }

    @Nested
    inner class UpdatingOrganisations {
        @Test
        fun `updating an organisation`() {
            val expiryTime = ZonedDateTime.parse("2019-12-04T15:11:59.537Z")
            val expiryTimeToString = expiryTime.format(DateTimeFormatter.ISO_INSTANT)

            val district = organisationRepository.save(
                OrganisationDetailsFactory.district(
                    name = "my district",
                    externalId = "123",
                    state = State(id = "FL", name = "Florida")
                )
            )

            mvc.perform(
                patch("/v1/organisations/${district.id.value}").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.UPDATE_ORGANISATIONS
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """{"accessExpiresOn": "$expiryTimeToString"}""".trimIndent()
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(jsonPath("$._links.edit.href", endsWith("/organisations/${district.id.value}")))
                .andExpect(jsonPath("$.id", equalTo(district.id.value)))
                .andExpect(jsonPath("$.accessExpiresOn", equalTo(expiryTimeToString)))
        }

        @Test
        fun `bad request when date is invalid`() {
            val district = organisationRepository.save(
                OrganisationDetailsFactory.district(
                    name = "my district",
                    externalId = "123",
                    state = State(id = "FL", name = "Florida")
                )
            )

            mvc.perform(
                patch("/v1/organisations/${district.id.value}").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.UPDATE_ORGANISATIONS
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """{"accessExpiresOn": "not a time"}""".trimIndent()
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
        }

        @Test
        fun `bad request when updating non existent organisation`() {
            val expiryTime = ZonedDateTime.now()
            val expiryTimeToString = expiryTime.format(DateTimeFormatter.ISO_INSTANT)

            mvc.perform(
                patch("/v1/organisations/not-an-organisation").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.UPDATE_ORGANISATIONS
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """{"accessExpiresOn": "$expiryTimeToString"}""".trimIndent()
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isNotFound)
        }

        @Test
        fun `returns forbidden when caller is not allowed to update organisations`() {
            val district = organisationRepository.save(
                OrganisationDetailsFactory.district(
                    name = "my district",
                    externalId = "123",
                    state = State(id = "FL", name = "Florida")
                )
            )

            val expiryTime = ZonedDateTime.now()
            val expiryTimeToString = expiryTime.format(DateTimeFormatter.ISO_INSTANT)
            mvc.perform(
                patch("/v1/organisations/${district.id.value}").asUser("an-outsider")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """{"accessExpiresOn": "$expiryTimeToString"}""".trimIndent()
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isForbidden)
        }
    }

    @Nested
    inner class FetchingOrganisationById {
        @Test
        fun `retrieves an api integration organisation by id`() {
            val organisationName = "Test Org"
            val organisation = organisationRepository.save(
                apiIntegration = OrganisationDetailsFactory.apiIntegration(
                    name = organisationName,
                    allowsOverridingUserIds = true
                ),
                accessRuleIds = listOf(AccessRuleId("A"), AccessRuleId("B"), AccessRuleId("C")),
                role = "ROLE_TEST_ORG"
            )

            mvc.perform(
                get("/v1/organisations/${organisation.id.value}")
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(jsonPath("$.accessRuleIds", containsInAnyOrder("A", "B", "C")))
                .andExpect(jsonPath("$.organisationDetails.name", equalTo(organisationName)))
                .andExpect(jsonPath("$.organisationDetails.allowsOverridingUserIds", equalTo(true)))
                .andExpect(jsonPath("$._links.self.href", endsWith("/organisations/${organisation.id.value}")))
                .andExpect(jsonPath("$._links.edit.href", endsWith("/organisations/${organisation.id.value}")))
        }

        @Test
        fun `returns a forbidden response when caller does not have view organisations role`() {
            mvc.perform(
                get("/v1/organisations/some-org")
                    .asUser("has-role@test.com")
            )
                .andExpect(MockMvcResultMatchers.status().isForbidden)
        }

        @Test
        fun `returns a 404 response when organisation is not found by id`() {
            mvc.perform(
                get("/v1/organisations/this-does-not-exist")
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(MockMvcResultMatchers.status().isNotFound)
        }
    }

    @Nested
    inner class GettingOrganisations {
        @Test
        fun `gets a page of all organisations when filters are empty`() {
            saveDistrict(district = OrganisationDetailsFactory.district(name = "district 1"))
            saveDistrict(district = OrganisationDetailsFactory.district(name = "district 2"))
            saveSchool(school = OrganisationDetailsFactory.school(name = "school 1"))

            mvc.perform(
                get("/v1/organisations").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.VIEW_ORGANISATIONS
                )
            ).andExpect(jsonPath("$._embedded.organisations", hasSize<Int>(3)))
                .andExpect(jsonPath("$._embedded.organisations[0].organisationDetails.name", equalTo("district 1")))
                .andExpect(jsonPath("$._embedded.organisations[1].organisationDetails.name", equalTo("district 2")))
                .andExpect(jsonPath("$._embedded.organisations[2].organisationDetails.name", equalTo("school 1")))
        }
    }
}
