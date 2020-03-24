package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
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
                OrganisationFactory.sample(
                    details = OrganisationDetailsFactory.district(
                        name = "my district",
                        externalId = "123",
                        state = State(id = "FL", name = "Florida")
                    ),
                    accessExpiresOn = expiryTime
                )
            )

            organisationRepository.save(
                OrganisationFactory.sample(
                    details = OrganisationDetailsFactory.school(
                        name = "my district school",
                        countryName = "USA",
                        state = State(id = "FL", name = "Florida"),
                        district = district
                    )
                )
            )
            organisationRepository.save(
                OrganisationFactory.sample(
                    details = OrganisationDetailsFactory.school(
                        name = "my independent school",
                        countryName = "USA",
                        state = State(id = "FL", name = "Florida"),
                        district = null
                    ),
                    accessExpiresOn = expiryTime
                )
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
                OrganisationFactory.sample(
                    details = OrganisationDetailsFactory.district(
                        name = "my district",
                        externalId = "123",
                        state = State(id = "FL", name = "Florida")
                    )
                )
            )
            val school = organisationRepository.save(
                OrganisationFactory.sample(
                    details = OrganisationDetailsFactory.school(
                        name = "my school",
                        country = Country.fromCode("GBR")
                    )
                )
            )

            mvc.perform(
                    get("/v1/organisations").asUserWithRoles("some-boclipper", UserRoles.VIEW_ORGANISATIONS)
                )
                .andExpect(jsonPath("$._embedded.organisations", hasSize<Int>(2)))
                .andExpect(
                    jsonPath(
                        "$._embedded.organisations[0].organisationDetails.name",
                        equalTo(district.details.name)
                    )
                )
                .andExpect(
                    jsonPath(
                        "$._embedded.organisations[1].organisationDetails.name",
                        equalTo(school.details.name)
                    )
                )
        }
    }

    @Nested
    inner class UpdatingOrganisations {
        @Test
        fun `updating an organisation`() {
            val expiryTime = ZonedDateTime.parse("2019-12-04T15:11:59.537Z")
            val expiryTimeToString = expiryTime.format(DateTimeFormatter.ISO_INSTANT)

            val district = organisationRepository.save(
                OrganisationFactory.sample(
                    details = OrganisationDetailsFactory.district(
                        name = "my district",
                        externalId = "123",
                        state = State(id = "FL", name = "Florida")
                    )
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
        fun `update organization domain`() {
            val givenDomain = "my-district.pl"
            val district = organisationRepository.save(
                OrganisationFactory.sample(
                    details = OrganisationDetailsFactory.district(
                        name = "my district",
                        externalId = "123",
                        state = State(id = "FL", name = "Florida")
                    )
                )
            )

            mvc.perform(
                    post("/v1/organisations/${district.id.value}").asUserWithRoles(
                            "some-boclipper",
                            UserRoles.UPDATE_ORGANISATIONS
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """ {"domain": "$givenDomain"} """.trimIndent()
                        )
                )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(jsonPath("$.id", equalTo(district.id.value)))
                .andExpect(jsonPath("$.organisationDetails.domain", equalTo(givenDomain)))
        }

        @Test
        fun `bad request when date is invalid`() {
            val district = organisationRepository.save(
                OrganisationFactory.sample(
                    details = OrganisationDetailsFactory.district(
                        name = "my district",
                        externalId = "123",
                        state = State(id = "FL", name = "Florida")
                    )
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
                OrganisationFactory.sample(
                    details = OrganisationDetailsFactory.district(
                        name = "my district",
                        externalId = "123",
                        state = State(id = "FL", name = "Florida")
                    )
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
    inner class AssociatingUsersWithOrganisation {
        @Test
        fun `associates users which should be in a given district to that district`() {
            userRepository.create(user = UserFactory.sample(identity = IdentityFactory.sample(username = "rebecca@district-domain.com")))
            val district =
                organisationRepository.save(
                    OrganisationFactory.sample(
                        details = OrganisationDetailsFactory.district(
                            domain = "district-domain.com"
                        )
                    )
                )

            mvc.perform(
                    post("/v1/organisations/${district.id.value}/associate").asUserWithRoles(UserRoles.UPDATE_ORGANISATIONS)
                )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(jsonPath("$._embedded.users", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.users[0].email", equalTo("rebecca@district-domain.com")))
                .andExpect(jsonPath("$._embedded.users[0].organisationAccountId", equalTo(district.id.value)))
        }

        @Test
        fun `associates users requires permissions to do so`() {
            mvc.perform(post("/v1/organisations/org-id/sync"))
                .andExpect(MockMvcResultMatchers.status().isForbidden)
        }
    }

    @Nested
    inner class FetchingOrganisationById {
        @Test
        fun `retrieves an api integration organisation by id`() {
            val organisationName = "Test Org"
            val contentPackage = saveContentPackage(ContentPackageFactory.sample())
            val organisation = organisationRepository.save(
                OrganisationFactory.sample(
                    details = OrganisationDetailsFactory.apiIntegration(
                        name = organisationName,
                        allowsOverridingUserIds = true
                    ),
                    role = "ROLE_TEST_ORG",
                    contentPackageId = contentPackage.id
                )
            )

            mvc.perform(
                    get("/v1/organisations/${organisation.id.value}")
                        .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
                )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(jsonPath("$.contentPackageId", equalTo(contentPackage.id.value)))
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
            saveDistrict(district = OrganisationDetailsFactory.district(name = "district 1", domain = "district.com"))
            saveDistrict(district = OrganisationDetailsFactory.district(name = "district 2"))
            saveSchool(school = OrganisationDetailsFactory.school(name = "school 1"))

            mvc
                .perform(get("/v1/organisations").asUserWithRoles("some-boclipper", UserRoles.VIEW_ORGANISATIONS))
                .andExpect(jsonPath("$._embedded.organisations", hasSize<Int>(3)))
                .andExpect(jsonPath("$._embedded.organisations[0].organisationDetails.name", equalTo("district 1")))
                .andExpect(jsonPath("$._embedded.organisations[0].organisationDetails.domain", equalTo("district.com")))
                .andExpect(jsonPath("$._embedded.organisations[1].organisationDetails.name", equalTo("district 2")))
                .andExpect(jsonPath("$._embedded.organisations[2].organisationDetails.name", equalTo("school 1")))
        }

        @Test
        fun `gets a page of all organisations matching name`() {
            saveDistrict(district = OrganisationDetailsFactory.district(name = "putname"))
            saveDistrict(district = OrganisationDetailsFactory.district(name = "pamdale"))

            mvc
                .perform(
                    get("/v1/organisations?name=pamdale").asUserWithRoles(
                        "some-boclipper",
                        UserRoles.VIEW_ORGANISATIONS
                    )
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.organisations", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.organisations[0].organisationDetails.name", equalTo("pamdale")))
        }
    }
}