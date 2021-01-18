package com.boclips.users.presentation.controllers

import com.boclips.users.api.request.CreateDistrictRequest
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.access.VideoType
import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.Prices
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.domain.service.UniqueId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.deal
import com.boclips.users.testsupport.factories.PriceFactory
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Currency

class OrganisationControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class FetchingIndependentOrganisations {
        @Test
        fun `returns a forbidden response when caller is not allowed to view organisations`() {
            mvc.perform(
                get("/v1/organisations?countryCode=USA")
                    .asUser("has-role@test.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `lists US schools and organisations with and without parent districts`() {
            val expiryTime = ZonedDateTime.parse("2019-12-04T15:11:59.531Z")

            val district = organisationRepository.save(
                OrganisationFactory.district(
                    name = "my district",
                    externalId = ExternalOrganisationId("123"),
                    address = Address(
                        country = Country.usa(),
                        state = State(id = "FL", name = "Florida")
                    ),
                    deal = deal(
                        accessExpiresOn = expiryTime
                    )
                )
            )

            organisationRepository.save(
                OrganisationFactory.school(
                    name = "my district school",
                    address = Address(
                        country = Country.usa(),
                        state = State(id = "FL", name = "Florida")
                    ),
                    district = district
                )
            )
            organisationRepository.save(
                OrganisationFactory.school(
                    name = "my independent school",
                    address = Address(
                        country = Country.usa(),
                        state = State(id = "FL", name = "Florida")
                    ),
                    district = null,
                    deal = deal(
                        accessExpiresOn = expiryTime
                    )
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
                .andExpect(
                    jsonPath(
                        "$._embedded.organisations[0]._links.edit.href",
                        endsWith("/v1/organisations/${district.id.value}")
                    )
                )
                .andExpect(jsonPath("$.page.totalElements", equalTo(3)))
                .andExpect(jsonPath("$.page.totalPages", equalTo(3)))
                .andExpect(jsonPath("$.page.size", equalTo(1)))
        }

        @Test
        fun `fetches all independent organisations when no countryCode is provided`() {
            val district = organisationRepository.save(
                OrganisationFactory.district(
                    name = "my district",
                    externalId = ExternalOrganisationId("123"),
                    address = Address(
                        state = State(id = "FL", name = "Florida")
                    )
                )
            )
            val school = organisationRepository.save(
                OrganisationFactory.school(
                    name = "my school",
                    address = Address(
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
                        equalTo(district.name)
                    )
                )
                .andExpect(
                    jsonPath(
                        "$._embedded.organisations[1].organisationDetails.name",
                        equalTo(school.name)
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
                OrganisationFactory.district(
                    name = "my district",
                    externalId = ExternalOrganisationId("123"),
                    address = Address(
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
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._links.edit.href", endsWith("/organisations/${district.id.value}")))
                .andExpect(jsonPath("$.id", equalTo(district.id.value)))
                .andExpect(jsonPath("$.accessExpiresOn", equalTo(expiryTimeToString)))
        }

        @Test
        fun `updating the contentPackageId of an organisation`() {
            val newContentPackageId = "5e5fe948b9abbe3602e52a61"

            val apiIntegration = organisationRepository.save(
                OrganisationFactory.apiIntegration(name = "my api integration")
            )
            mvc.perform(
                patch("/v1/organisations/${apiIntegration.id.value}").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.UPDATE_ORGANISATIONS
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """{"contentPackageId": "$newContentPackageId"}"""
                    )
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._links.edit.href", endsWith("/organisations/${apiIntegration.id.value}")))
                .andExpect(jsonPath("$.id", equalTo(apiIntegration.id.value)))
                .andExpect(jsonPath("$.contentPackageId", equalTo(newContentPackageId)))
        }

        @Test
        fun `updating the billing of an organisation`() {
            val newBilling = true

            val apiIntegration = organisationRepository.save(
                OrganisationFactory.apiIntegration(
                    name = "my api integration",
                    deal = Deal(
                        billing = false,
                        accessExpiresOn = null
                    )
                )
            )

            mvc.perform(
                patch("/v1/organisations/${apiIntegration.id.value}").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.UPDATE_ORGANISATIONS
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """{"billing": "$newBilling"}"""
                    )
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._links.edit.href", endsWith("/organisations/${apiIntegration.id.value}")))
                .andExpect(jsonPath("$.id", equalTo(apiIntegration.id.value)))
                .andExpect(jsonPath("$.billing", equalTo(newBilling)))
        }

        @Test
        fun `update organization domain`() {
            val givenDomain = "my-district.pl"
            val district = organisationRepository.save(
                OrganisationFactory.district(
                    name = "my district",
                    externalId = ExternalOrganisationId("123"),
                    address = Address(
                        state = State(id = "FL", name = "Florida")
                    ),
                    features = mapOf(Feature.TEACHERS_HOME_BANNER to false)
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
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id", equalTo(district.id.value)))
                .andExpect(jsonPath("$.organisationDetails.domain", equalTo(givenDomain)))
                .andExpect(jsonPath("$.organisationDetails.features.TEACHERS_HOME_BANNER", equalTo(false)))
        }

        @Test
        fun `updating organization features`() {
            val org = organisationRepository.save(
                OrganisationFactory.apiIntegration(
                    name = "Integration X",
                    features = mapOf(Feature.USER_DATA_HIDDEN to false)
                )
            )

            mvc.perform(
                post("/v1/organisations/${org.id.value}").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.UPDATE_ORGANISATIONS
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """ {"features":  { 
                                "LTI_COPY_RESOURCE_LINK" : "false",
                                "LTI_SLS_TERMS_BUTTON" : "false",
                                "TEACHERS_HOME_BANNER" : "false",
                                "TEACHERS_HOME_SUGGESTED_VIDEOS" : "false",
                                "TEACHERS_HOME_PROMOTED_COLLECTIONS" : "false",
                                "TEACHERS_SUBJECTS" : "false",
                                "USER_DATA_HIDDEN" : "true" }
                            } """.trimIndent()
                    )
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id", equalTo(org.id.value)))
                .andExpect(jsonPath("$.organisationDetails.features.LTI_COPY_RESOURCE_LINK", equalTo(false)))
                .andExpect(jsonPath("$.organisationDetails.features.LTI_SLS_TERMS_BUTTON", equalTo(false)))
                .andExpect(jsonPath("$.organisationDetails.features.TEACHERS_HOME_BANNER", equalTo(false)))
                .andExpect(jsonPath("$.organisationDetails.features.TEACHERS_HOME_SUGGESTED_VIDEOS", equalTo(false)))
                .andExpect(
                    jsonPath(
                        "$.organisationDetails.features.TEACHERS_HOME_PROMOTED_COLLECTIONS",
                        equalTo(false)
                    )
                )
                .andExpect(jsonPath("$.organisationDetails.features.TEACHERS_SUBJECTS", equalTo(false)))
                .andExpect(jsonPath("$.organisationDetails.features.USER_DATA_HIDDEN", equalTo(true)))
        }

        @Test
        fun `bad request if trying to set invalid feature`() {
            val org = organisationRepository.save(
                OrganisationFactory.apiIntegration(
                    name = "Integration X",
                    features = mapOf(Feature.USER_DATA_HIDDEN to false)
                )
            )

            mvc.perform(
                post("/v1/organisations/${org.id.value}").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.UPDATE_ORGANISATIONS
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """ {"features":  { 
                                "NONEXISTENTFEATURE" : "false",
                                "USER_DATA_HIDDEN" : "true" }
                            } """.trimIndent()
                    )
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `bad request when date is invalid`() {
            val district = organisationRepository.save(
                OrganisationFactory.district(
                    name = "my district",
                    externalId = ExternalOrganisationId("123"),
                    address = Address(
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
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `bad request when updating non existent organisation`() {
            val expiryTime = ZonedDateTime.now()
            val expiryTimeToString = expiryTime.format(DateTimeFormatter.ISO_INSTANT)

            mvc.perform(
                patch("/v1/organisations/${UniqueId()}").asUserWithRoles(
                    "some-boclipper",
                    UserRoles.UPDATE_ORGANISATIONS
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """{"accessExpiresOn": "$expiryTimeToString"}""".trimIndent()
                    )
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `returns forbidden when caller is not allowed to update organisations`() {
            val district = organisationRepository.save(
                OrganisationFactory.district(
                    name = "my district",
                    externalId = ExternalOrganisationId("123"),
                    address = Address(
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
                .andExpect(status().isForbidden)
        }
    }

    @Nested
    inner class AssociatingUsersWithOrganisation {
        @Test
        fun `associates users which should be in a given district to that district`() {
            userRepository.create(user = UserFactory.sample(identity = IdentityFactory.sample(username = "rebecca@district-domain.com")))
            val district =
                organisationRepository.save(
                    OrganisationFactory.district(
                        domain = "district-domain.com"
                    )
                )

            mvc.perform(
                post("/v1/organisations/${district.id.value}/associate").asUserWithRoles(UserRoles.UPDATE_ORGANISATIONS)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.users", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.users[0].email", equalTo("rebecca@district-domain.com")))
                .andExpect(jsonPath("$._embedded.users[0].organisation.id", equalTo(district.id.value)))
        }

        @Test
        fun `associates users requires permissions to do so`() {
            mvc.perform(post("/v1/organisations/org-id/sync"))
                .andExpect(status().isForbidden)
        }
    }

    @Nested
    inner class FetchingOrganisationById {
        @Test
        fun `retrieves an api integration organisation by id`() {
            val organisationName = "Test Org"
            val contentPackage = saveContentPackage(ContentPackageFactory.sample())
            val organisation = organisationRepository.save(
                OrganisationFactory.apiIntegration(
                    name = organisationName,
                    allowsOverridingUserId = true,
                    role = "ROLE_TEST_ORG",
                    deal = deal(
                        contentPackageId = contentPackage.id,
                        prices = Prices(
                            videoTypePrices = mapOf(
                                VideoType.INSTRUCTIONAL to PriceFactory.sample(amount = BigDecimal.ONE),
                                VideoType.NEWS to PriceFactory.sample(amount = BigDecimal.TEN),
                                VideoType.STOCK to PriceFactory.sample(amount = BigDecimal.ZERO)
                            )
                        )
                    )
                )
            )

            mvc.perform(
                get("/v1/organisations/${organisation.id.value}")
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.contentPackageId", equalTo(contentPackage.id.value)))
                .andExpect(jsonPath("$.organisationDetails.name", equalTo(organisationName)))
                .andExpect(jsonPath("$.organisationDetails.allowsOverridingUserIds", equalTo(true)))
                .andExpect(jsonPath("$.deal.billing", equalTo(false)))
                .andExpect(jsonPath("$.deal.contentPackageId", equalTo(contentPackage.id.value)))
                .andExpect(jsonPath("$.deal.prices.videoTypePrices.INSTRUCTIONAL.amount", equalTo("1")))
                .andExpect(jsonPath("$.deal.prices.videoTypePrices.INSTRUCTIONAL.currency", equalTo("USD")))
                .andExpect(jsonPath("$.deal.prices.videoTypePrices.NEWS.amount", equalTo("10")))
                .andExpect(jsonPath("$.deal.prices.videoTypePrices.NEWS.currency", equalTo("USD")))
                .andExpect(jsonPath("$.deal.prices.videoTypePrices.STOCK.amount", equalTo("0")))
                .andExpect(jsonPath("$.deal.prices.videoTypePrices.STOCK.currency", equalTo("USD")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/organisations/${organisation.id.value}")))
                .andExpect(jsonPath("$._links.edit.href", endsWith("/organisations/${organisation.id.value}")))
        }

        @Test
        fun `retrieves an api integration organisation by id when no prices are defined`() {
            val organisationName = "Test Org With No Deal Prices"
            val contentPackage = saveContentPackage(ContentPackageFactory.sample())
            val organisation = organisationRepository.save(
                OrganisationFactory.apiIntegration(
                    name = organisationName,
                    allowsOverridingUserId = true,
                    role = "ROLE_TEST_ORG",
                    deal = deal(
                        contentPackageId = contentPackage.id,
                        prices = null
                    )
                )
            )

            mvc.perform(
                get("/v1/organisations/${organisation.id.value}")
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.contentPackageId", equalTo(contentPackage.id.value)))
                .andExpect(jsonPath("$.organisationDetails.name", equalTo(organisationName)))
                .andExpect(jsonPath("$.organisationDetails.allowsOverridingUserIds", equalTo(true)))
                .andExpect(jsonPath("$.deal.billing", equalTo(false)))
                .andExpect(jsonPath("$.deal.contentPackageId", equalTo(contentPackage.id.value)))
                .andExpect(jsonPath("$.organisationDetails.deal.prices").doesNotExist())
                .andExpect(jsonPath("$._links.self.href", endsWith("/organisations/${organisation.id.value}")))
                .andExpect(jsonPath("$._links.edit.href", endsWith("/organisations/${organisation.id.value}")))
        }

        @Test
        fun `returns a forbidden response when caller does not have view organisations role`() {
            mvc.perform(
                get("/v1/organisations/some-org")
                    .asUser("has-role@test.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `returns a 404 response when organisation is not found by id`() {
            mvc.perform(
                get("/v1/organisations/${UniqueId()}")
                    .asUserWithRoles("has-role@test.com", UserRoles.VIEW_ORGANISATIONS)
            )
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    inner class GettingOrganisations {
        @Test
        fun `gets a page of all organisations when filters are empty`() {
            saveOrganisation(OrganisationFactory.district(name = "district 1", domain = "district.com"))
            saveOrganisation(OrganisationFactory.district(name = "district 2"))
            saveOrganisation(OrganisationFactory.school(name = "school 1"))

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
            saveOrganisation(OrganisationFactory.district(name = "putname"))
            saveOrganisation(OrganisationFactory.district(name = "pamdale"))

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

        @Test
        fun `gets all organisations with custom price`() {
            val organisationWithPrice = saveOrganisation(
                OrganisationFactory.apiIntegration(
                    deal = OrganisationFactory.pricedDeal()
                )
            )

            saveOrganisation(OrganisationFactory.apiIntegration(deal = deal(prices = null)))

            mvc.perform(
                get("/v1/organisations?hasCustomPrices=true").asUserWithRoles(
                    "me",
                    UserRoles.VIEW_ORGANISATIONS
                )
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.organisations", hasSize<Int>(1)))
                .andExpect(
                    jsonPath(
                        "$._embedded.organisations[0].organisationDetails.id",
                        equalTo(organisationWithPrice.id.value)
                    )
                )
        }
    }

    @Nested
    inner class CreatingOrganisations {
        @Test
        fun `creates a district organisation with given name and content package`() {
            val name = "the_district_name"
            val contentPackage = ContentPackageFactory.sample()
            contentPackageRepository.save(contentPackage)

            val request = CreateDistrictRequest()
            request.name = name
            request.contentPackageId = contentPackage.id.value
            request.type = "DISTRICT"

            mvc
                .perform(
                    post("/v1/organisations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """{
                                "name": "the_district_name",
                                "type": "DISTRICT",
                                "contentPackageId": "${contentPackage.id.value}"
                                }"""
                        )
                        .asUserWithRoles("some-boclipper", UserRoles.INSERT_ORGANISATIONS)
                )
                .andExpect(jsonPath("$.organisationDetails.name", equalTo("the_district_name")))
                .andExpect(jsonPath("$.contentPackageId", equalTo(contentPackage.id.value)))
        }

        @Test
        fun `returns 409 when organisation with given name already exists`() {
            saveOrganisation(OrganisationFactory.district(name = "the_district_name"))
            val contentPackage = ContentPackageFactory.sample()
            contentPackageRepository.save(contentPackage)
            mvc
                .perform(
                    post("/v1/organisations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """{
                                "name": "the_district_name",
                                "type": "DISTRICT",
                                "contentPackageId": "${contentPackage.id.value}"
                                }"""
                        )
                        .asUserWithRoles("some-boclipper", UserRoles.INSERT_ORGANISATIONS)
                )
                .andExpect(status().isConflict)
        }

        @Test
        fun `returns 400 when organisation type is not district`() {
            saveOrganisation(OrganisationFactory.district(name = "the_district_name"))
            val contentPackage = ContentPackageFactory.sample()
            contentPackageRepository.save(contentPackage)
            mvc
                .perform(
                    post("/v1/organisations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """{
                                "name": "the_district_name",
                                "type": "SCHOOL",
                                "contentPackageId": "${contentPackage.id.value}"
                                }"""
                        )
                        .asUserWithRoles("some-boclipper", UserRoles.INSERT_ORGANISATIONS)
                )
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `returns 403 when called without INSERT_ORGANISATIONS role`() {
            saveOrganisation(OrganisationFactory.district(name = "the_district_name"))
            val contentPackage = ContentPackageFactory.sample()
            contentPackageRepository.save(contentPackage)
            mvc
                .perform(
                    post("/v1/organisations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """{
                                "name": "the_district_name",
                                "type": "SCHOOL",
                                "contentPackageId": "${contentPackage.id.value}"
                                }"""
                        )
                )
                .andExpect(status().isForbidden)
        }
    }
}
