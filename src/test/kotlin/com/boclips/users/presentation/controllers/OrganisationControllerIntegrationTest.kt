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
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Stream

class OrganisationControllerIntegrationTest : AbstractSpringIntegrationTest() {

    class ResourceProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
            return Stream.of(
                Arguments.of("accounts"),
                Arguments.of("organisations")
            )
        }
    }

    @Nested
    inner class FetchingIndependentAccounts {
        @ParameterizedTest
        @ArgumentsSource(ResourceProvider::class)
        fun `returns a forbidden response when caller is not allowed to view organisations`(resource: String) {
            mvc.perform(
                get("/v1/$resource?countryCode=USA")
                    .asUser("has-role@test.com")
            )
                .andExpect(MockMvcResultMatchers.status().isForbidden)
        }

        @ParameterizedTest
        @ArgumentsSource(ResourceProvider::class)
        fun `lists all independent US schools and organisations`(resource: String) {
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
                get("/v1/$resource?countryCode=USA&page=0&size=1").asUserWithRoles(
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

        @ParameterizedTest
        @ArgumentsSource(ResourceProvider::class)
        fun `fetches all independent organisations when no countryCode is provided`(resource: String) {
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
                get("/v1/$resource").asUserWithRoles("some-boclipper", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(jsonPath("$._embedded.organisations", hasSize<Int>(2)))
                .andExpect(jsonPath("$._embedded.organisations[0].organisationDetails.name", equalTo(district.organisation.name)))
                .andExpect(jsonPath("$._embedded.organisations[1].organisationDetails.name", equalTo(school.organisation.name)))
        }
    }

    @Nested
    inner class UpdatingOrganisations {
        @ParameterizedTest
        @ArgumentsSource(ResourceProvider::class)
        fun `updating an organisation`(resource: String) {
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
                patch("/v1/$resource/${district.id.value}").asUserWithRoles(
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

        @ParameterizedTest
        @ArgumentsSource(ResourceProvider::class)
        fun `bad request when date is invalid`(resource: String) {
            val district = organisationRepository.save(
                OrganisationDetailsFactory.district(
                    name = "my district",
                    externalId = "123",
                    state = State(id = "FL", name = "Florida")
                )
            )

            mvc.perform(
                patch("/v1/$resource/${district.id.value}").asUserWithRoles(
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

        @ParameterizedTest
        @ArgumentsSource(ResourceProvider::class)
        fun `bad request when updating non existent organisation`(resource: String) {
            val expiryTime = ZonedDateTime.now()
            val expiryTimeToString = expiryTime.format(DateTimeFormatter.ISO_INSTANT)

            mvc.perform(
                patch("/v1/$resource/not-an-organisation").asUserWithRoles(
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

        @ParameterizedTest
        @ArgumentsSource(ResourceProvider::class)
        fun `returns forbidden when caller is not allowed to update organisations`(resource: String) {
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
                patch("/v1/$resource/${district.id.value}").asUser("an-outsider")
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
        @ParameterizedTest
        @ArgumentsSource(ResourceProvider::class)
        fun `retrieves an api integration organisation by id`(resource: String) {
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
                get("/v1/$resource/${organisation.id.value}")
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(jsonPath("$.accessRuleIds", containsInAnyOrder("A", "B", "C")))
                .andExpect(jsonPath("$.organisationDetails.name", equalTo(organisationName)))
                .andExpect(jsonPath("$.organisationDetails.allowsOverridingUserIds", equalTo(true)))
                .andExpect(jsonPath("$._links.self.href", endsWith("/organisations/${organisation.id.value}")))
                .andExpect(jsonPath("$._links.edit.href", endsWith("/organisations/${organisation.id.value}")))
        }

        @ParameterizedTest
        @ArgumentsSource(ResourceProvider::class)
        fun `returns a forbidden response when caller does not have view organisations role`(resource: String) {
            mvc.perform(
                get("/v1/$resource/some-org")
                    .asUser("has-role@test.com")
            )
                .andExpect(MockMvcResultMatchers.status().isForbidden)
        }

        @ParameterizedTest
        @ArgumentsSource(ResourceProvider::class)
        fun `returns a 404 response when organisation is not found by id`(resource: String) {
            mvc.perform(
                get("/v1/$resource/this-does-not-exist")
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(MockMvcResultMatchers.status().isNotFound)
        }
    }

    @Nested
    inner class GettingOrganisations {
        @ParameterizedTest
        @ArgumentsSource(ResourceProvider::class)
        fun `gets a page of all organisations when filters are empty`(resource: String) {
            saveDistrict(district = OrganisationDetailsFactory.district(name = "district 1"))
            saveDistrict(district = OrganisationDetailsFactory.district(name = "district 2"))
            saveSchool(school = OrganisationDetailsFactory.school(name = "school 1"))

            mvc.perform(
                get("/v1/$resource").asUserWithRoles(
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
