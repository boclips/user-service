package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.OrganisationFactory
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

class AccountControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class FetchingIndependentAccounts {
        @Test
        fun `returns a forbidden response when caller is not allowed to view organisations`() {
            mvc.perform(
                get("/v1/independent-accounts?countryCode=USA")
                    .asUser("has-role@test.com")
            )
                .andExpect(MockMvcResultMatchers.status().isForbidden)
        }

        @Test
        fun `lists all independent US schools and organisations`() {
            val expiryTime = ZonedDateTime.parse("2019-12-04T15:11:59.531Z")
            val expiryTimeToString = expiryTime.format(DateTimeFormatter.ISO_INSTANT)

            val district = accountRepository.save(
                OrganisationFactory.district(
                    name = "my district",
                    externalId = "123",
                    state = State(id = "FL", name = "Florida")
                ),
                accessExpiresOn = expiryTime
            )
            accountRepository.save(
                school = OrganisationFactory.school(
                    name = "my district school",
                    countryName = "USA",
                    state = State(id = "FL", name = "Florida"),
                    district = district
                )
            )
            val school = accountRepository.save(
                school = OrganisationFactory.school(
                    name = "my independent school",
                    countryName = "USA",
                    state = State(id = "FL", name = "Florida"),
                    district = null
                ),
                accessExpiresOn = expiryTime
            )
            mvc.perform(
                get("/v1/independent-accounts?countryCode=USA").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.VIEW_ORGANISATIONS
                )
            )
                .andExpect(jsonPath("$._embedded.account", hasSize<Int>(2)))
                .andExpect(jsonPath("$._embedded.account[0].organisation.name", equalTo(district.organisation.name)))
                .andExpect(
                    jsonPath(
                        "$._embedded.account[0].organisation.type",
                        equalTo(district.organisation.type().toString())
                    )
                )
                .andExpect(jsonPath("$._embedded.account[0].accessExpiresOn", equalTo(expiryTimeToString)))
                .andExpect(
                    jsonPath(
                        "$._embedded.account[0]._links.edit.href",
                        endsWith("/v1/accounts/${district.id.value}")
                    )
                )
                .andExpect(jsonPath("$._embedded.account[1].organisation.name", equalTo(school.organisation.name)))
        }

        @Test
        fun `it paginates independent US schools and organisations`() {
            val expiryTime = ZonedDateTime.parse("2019-12-04T15:11:59.531Z")
            val expiryTimeToString = expiryTime.format(DateTimeFormatter.ISO_INSTANT)

            val district = accountRepository.save(
                OrganisationFactory.district(
                    name = "my district",
                    externalId = "123",
                    state = State(id = "FL", name = "Florida")
                ),
                accessExpiresOn = expiryTime
            )
            accountRepository.save(
                school = OrganisationFactory.school(
                    name = "my district school",
                    countryName = "USA",
                    state = State(id = "FL", name = "Florida"),
                    district = district
                )
            )
            accountRepository.save(
                school = OrganisationFactory.school(
                    name = "my independent school",
                    countryName = "USA",
                    state = State(id = "FL", name = "Florida"),
                    district = null
                ),
                accessExpiresOn = expiryTime
            )
            mvc.perform(
                get("/v1/independent-accounts?countryCode=USA&size=1").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.VIEW_ORGANISATIONS
                )
            )
                .andExpect(jsonPath("$._embedded.account", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.account[0].organisation.name", equalTo(district.organisation.name)))
                .andExpect(
                    jsonPath(
                        "$._embedded.account[0].organisation.type",
                        equalTo(district.organisation.type().toString())
                    )
                )
                .andExpect(jsonPath("$._embedded.account[0].accessExpiresOn", equalTo(expiryTimeToString)))
                .andExpect(
                    jsonPath(
                        "$._embedded.account[0]._links.edit.href",
                        endsWith("/v1/accounts/${district.id.value}")
                    )
                )
        }

        @Test
        fun `it provides a next link when there are further pages of independent US schools and organisations`() {
            val expiryTime = ZonedDateTime.parse("2019-12-04T15:11:59.531Z")
            val expiryTimeToString = expiryTime.format(DateTimeFormatter.ISO_INSTANT)

            val district = accountRepository.save(
                OrganisationFactory.district(
                    name = "my district",
                    externalId = "123",
                    state = State(id = "FL", name = "Florida")
                ),
                accessExpiresOn = expiryTime
            )
            accountRepository.save(
                school = OrganisationFactory.school(
                    name = "my district school",
                    countryName = "USA",
                    state = State(id = "FL", name = "Florida"),
                    district = district
                )
            )
            accountRepository.save(
                school = OrganisationFactory.school(
                    name = "my independent school",
                    countryName = "USA",
                    state = State(id = "FL", name = "Florida"),
                    district = null
                ),
                accessExpiresOn = expiryTime
            )
            mvc.perform(
                get("/v1/independent-accounts?countryCode=USA&size=1").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.VIEW_ORGANISATIONS
                )
            )
                .andExpect(jsonPath("$._embedded.account", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.account[0].organisation.name", equalTo(district.organisation.name)))
                .andExpect(
                    jsonPath(
                        "$._embedded.account[0].organisation.type",
                        equalTo(district.organisation.type().toString())
                    )
                )
                .andExpect(jsonPath("$._embedded.account[0].accessExpiresOn", equalTo(expiryTimeToString)))
                .andExpect(
                    jsonPath(
                        "$._embedded.account[0]._links.edit.href",
                        endsWith("/v1/accounts/${district.id.value}")
                    )
                )
                .andExpect(jsonPath("$.page.size", equalTo(1)))
                .andExpect(jsonPath("$.page.totalElements", equalTo(2)))
                .andExpect(jsonPath("$.page.totalPages", equalTo(2)))
                .andExpect(
                    jsonPath(
                        "$._links.next.href",
                        endsWith("/v1/independent-accounts?countryCode=USA&size=1&page=1")
                    )
                )
        }

        @Test
        fun `fetches all independent accounts when no countryCode is provided`() {
            val district = accountRepository.save(
                OrganisationFactory.district(
                    name = "my district",
                    externalId = "123",
                    state = State(id = "FL", name = "Florida")
                )
            )
            val school = accountRepository.save(
                OrganisationFactory.school(
                    name = "my school",
                    country = Country.fromCode("GBR")
                )
            )

            mvc.perform(
                get("/v1/independent-accounts").asUserWithRoles("some-boclipper", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(jsonPath("$._embedded.account", hasSize<Int>(2)))
                .andExpect(jsonPath("$._embedded.account[0].organisation.name", equalTo(district.organisation.name)))
                .andExpect(jsonPath("$._embedded.account[1].organisation.name", equalTo(school.organisation.name)))
        }
    }

    @Nested
    inner class UpdatingAccounts {
        @Test
        fun `updating an organisation account`() {
            val expiryTime = ZonedDateTime.parse("2019-12-04T15:11:59.537Z")
            val expiryTimeToString = expiryTime.format(DateTimeFormatter.ISO_INSTANT)

            val district = accountRepository.save(
                OrganisationFactory.district(
                    name = "my district",
                    externalId = "123",
                    state = State(id = "FL", name = "Florida")
                )
            )

            mvc.perform(
                patch("/v1/accounts/${district.id.value}").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.UPDATE_ORGANISATIONS
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """{"accessExpiresOn": "$expiryTimeToString"}""".trimIndent()
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(jsonPath("$._links.edit.href", endsWith("/accounts/${district.id.value}")))
                .andExpect(jsonPath("$.id", equalTo(district.id.value)))
                .andExpect(jsonPath("$.accessExpiresOn", equalTo(expiryTimeToString)))
        }

        @Test
        fun `bad request when date is invalid`() {
            val district = accountRepository.save(
                OrganisationFactory.district(
                    name = "my district",
                    externalId = "123",
                    state = State(id = "FL", name = "Florida")
                )
            )

            mvc.perform(
                patch("/v1/accounts/${district.id.value}").asUserWithRoles(
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
                patch("/v1/accounts/not-an-organisation").asUserWithRoles(
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
            val district = accountRepository.save(
                OrganisationFactory.district(
                    name = "my district",
                    externalId = "123",
                    state = State(id = "FL", name = "Florida")
                )
            )

            val expiryTime = ZonedDateTime.now()
            val expiryTimeToString = expiryTime.format(DateTimeFormatter.ISO_INSTANT)
            mvc.perform(
                patch("/v1/accounts/${district.id.value}").asUser("an-outsider")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """{"accessExpiresOn": "$expiryTimeToString"}""".trimIndent()
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isForbidden)
        }
    }

    @Nested
    inner class FetchingOrganisationsAccountById {
        @Test
        fun `retrieves an api integration organisation account by id`() {
            val organisationName = "Test Org"
            val organisation = accountRepository.save(
                apiIntegration = OrganisationFactory.apiIntegration(
                    name = organisationName,
                    allowsOverridingUserIds = true
                ),
                contractIds = listOf(ContractId("A"), ContractId("B"), ContractId("C")),
                role = "ROLE_TEST_ORG"
            )

            mvc.perform(
                get("/v1/accounts/${organisation.id.value}")
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(jsonPath("$.contractIds", containsInAnyOrder("A", "B", "C")))
                .andExpect(jsonPath("$.organisation.name", equalTo(organisationName)))
                .andExpect(jsonPath("$.organisation.allowsOverridingUserIds", equalTo(true)))
                .andExpect(jsonPath("$._links.self.href", endsWith("/accounts/${organisation.id.value}")))
                .andExpect(jsonPath("$._links.edit.href", endsWith("/accounts/${organisation.id.value}")))
        }

        @Test
        fun `returns a forbidden response when caller does not have view organisations role`() {
            mvc.perform(
                get("/v1/accounts/some-org")
                    .asUser("has-role@test.com")
            )
                .andExpect(MockMvcResultMatchers.status().isForbidden)
        }

        @Test
        fun `returns a 404 response when organisation account is not found by id`() {
            mvc.perform(
                get("/v1/accounts/this-does-not-exist")
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(MockMvcResultMatchers.status().isNotFound)
        }
    }

    @Nested
    inner class GettingAccounts {
        @Test
        fun `gets a page of all accounts when filters are empty`() {
            saveDistrict(district = OrganisationFactory.district(name = "district 1"))
            saveDistrict(district = OrganisationFactory.district(name = "district 2"))
            saveSchool(school = OrganisationFactory.school(name = "school 1"))

            mvc.perform(
                get("/v1/accounts").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.VIEW_ORGANISATIONS
                )
            ).andExpect(jsonPath("$._embedded.account", hasSize<Int>(3)))
                .andExpect(jsonPath("$._embedded.account[0].organisation.name", equalTo("district 1")))
                .andExpect(jsonPath("$._embedded.account[1].organisation.name", equalTo("district 2")))
                .andExpect(jsonPath("$._embedded.account[2].organisation.name", equalTo("school 1")))
        }

        @Test
        fun `gets parent accounts when parent account filter present`(){}

        @Test
        fun `returns no results when there ware no matching organisations`(){}

        @Test
        fun `returns a forbidden response when user is not allowed to view organisations`(){}

    }


}
