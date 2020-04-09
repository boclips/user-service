package com.boclips.users.presentation.controllers.accessrules

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.ContentPartnerId
import com.boclips.users.domain.model.contentpackage.DistributionMethod
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.domain.model.contentpackage.VideoType
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.AccessRuleFactory
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder

class AccessRulesControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class SearchAccessRules {
        @Test
        fun `returns a 403 response when caller does not have a VIEW_ACCESS_RULES role`() {
            mvc.perform(
                    get("/v1/access-rules?name=Super+Contract")
                        .asUser("cant-view-access-rules@hacker.com")
                )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `returns given access rule on the list when the name matches`() {
            val accessRuleName = "Super contract"
            val accessRule = accessRuleRepository.save(AccessRule.IncludedCollections(id = AccessRuleId(), name = accessRuleName, collectionIds = listOf(CollectionId("A"))))

            mvc.perform(
                    get(
                        UriComponentsBuilder.fromUriString("/v1/access-rules")
                            .queryParam("name", accessRuleName)
                            .build()
                            .toUri()
                    )
                        .asUserWithRoles("access-rules-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.accessRules", hasSize<Any>(1)))
                .andExpect(jsonPath("$._embedded.accessRules[0].type", equalTo("IncludedCollections")))
                .andExpect(jsonPath("$._embedded.accessRules[0].type", equalTo("IncludedCollections")))
                .andExpect(jsonPath("$._embedded.accessRules[0].name", equalTo(accessRuleName)))
                .andExpect(jsonPath("$._embedded.accessRules[0].collectionIds", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.accessRules[0].collectionIds[0]", equalTo("A")))
                .andExpect(
                    jsonPath(
                        "$._embedded.accessRules[0]._links.addCollection.href",
                        endsWith("/v1/included-content-access-rules/${accessRule.id.value}/collections/{collectionId}")
                    )
                )
                .andExpect(jsonPath("$._embedded.accessRules[0]._links.addCollection.templated", equalTo(true)))
                .andExpect(
                    jsonPath(
                        "$._embedded.accessRules[0]._links.removeCollection.href",
                        endsWith("/v1/included-content-access-rules/${accessRule.id.value}/collections/{collectionId}")
                    )
                )
                .andExpect(jsonPath("$._embedded.accessRules[0]._links.removeCollection.templated", equalTo(true)))
                .andExpect(
                    jsonPath(
                        "$._embedded.accessRules[0]._links.self.href",
                        endsWith("/v1/access-rules/${accessRule.id.value}")
                    )
                )
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/access-rules?name=Super%20contract")))
        }

        @Test
        fun `returns an empty list when access rule is not found by name`() {
            accessRuleRepository.save(AccessRule.IncludedCollections(id = AccessRuleId(), name = "Super contract", collectionIds = listOf(CollectionId("A"))))

            mvc.perform(
                    get(
                        UriComponentsBuilder.fromUriString("/v1/access-rules")
                            .queryParam("name", "this does not exist")
                            .build()
                            .toUri()
                    )
                        .asUserWithRoles("access-rules-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.accessRules", hasSize<Any>(0)))
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/access-rules?name=this%20does%20not%20exist")))
        }

        @Test
        fun `returns an empty list when lookup is done with a blank parameter`() {
            accessRuleRepository.save(AccessRule.IncludedCollections(id = AccessRuleId(), name = "Super contract", collectionIds = listOf(CollectionId("A"))))

            mvc.perform(
                    get(
                        UriComponentsBuilder.fromUriString("/v1/access-rules")
                            .queryParam("name", "")
                            .build()
                            .toUri()
                    )
                        .asUserWithRoles("access-rules-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.accessRules", hasSize<Any>(0)))
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/access-rules?name=")))
        }

        @Test
        fun `can fetch all access rules in the system when name query parameter is not provided`() {
            val firstAccessRuleName = "first"
            val firstAccessRule = accessRuleRepository.save(AccessRule.IncludedCollections(id = AccessRuleId(), name = firstAccessRuleName, collectionIds = listOf(CollectionId("A"))))

            val secondAccessRuleName = "second"
            val secondAccessRule = accessRuleRepository.save(AccessRule.IncludedCollections(id = AccessRuleId(), name = secondAccessRuleName, collectionIds = listOf(CollectionId("B"))))

            mvc.perform(
                    get("/v1/access-rules")
                        .asUserWithRoles("access-rules-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.accessRules", hasSize<Any>(2)))
                .andExpect(jsonPath("$._embedded.accessRules[0].type", equalTo("IncludedCollections")))
                .andExpect(jsonPath("$._embedded.accessRules[0].name", equalTo(firstAccessRuleName)))
                .andExpect(jsonPath("$._embedded.accessRules[0].collectionIds", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.accessRules[0].collectionIds[0]", equalTo("A")))
                .andExpect(
                    jsonPath(
                        "$._embedded.accessRules[0]._links.self.href",
                        endsWith("/v1/access-rules/${firstAccessRule.id.value}")
                    )
                )
                .andExpect(jsonPath("$._embedded.accessRules[1].type", equalTo("IncludedCollections")))
                .andExpect(jsonPath("$._embedded.accessRules[1].name", equalTo(secondAccessRuleName)))
                .andExpect(jsonPath("$._embedded.accessRules[1].collectionIds", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.accessRules[1].collectionIds[0]", equalTo("B")))
                .andExpect(
                    jsonPath(
                        "$._embedded.accessRules[1]._links.self.href",
                        endsWith("/v1/access-rules/${secondAccessRule.id.value}")
                    )
                )
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/access-rules{?name}")))
        }

        @Test
        fun `can fetch ExcludedVideos access rules`() {
            val accessRule = accessRuleRepository.save(
                AccessRuleFactory.sampleExcludedVideosAccessRule(
                    name = "BadVideos",
                    videoIds = listOf(VideoId("A"), VideoId("B"))
                )
            )

            mvc.perform(
                    get("/v1/access-rules")
                        .asUserWithRoles("access-rules-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.accessRules", hasSize<Any>(1)))
                .andExpect(jsonPath("$._embedded.accessRules[0].type", equalTo("ExcludedVideos")))
                .andExpect(jsonPath("$._embedded.accessRules[0].name", equalTo(accessRule.name)))
                .andExpect(jsonPath("$._embedded.accessRules[0].videoIds", hasSize<Int>(2)))
                .andExpect(jsonPath("$._embedded.accessRules[0].videoIds[*]", containsInAnyOrder("A", "B")))
                .andExpect(
                    jsonPath(
                        "$._embedded.accessRules[0]._links.self.href",
                        endsWith("/v1/access-rules/${accessRule.id.value}")
                    )
                )
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/access-rules{?name}")))
        }

        @Test
        fun `can fetch ExcludedVideoTypes access rules`() {
            val accessRule = accessRuleRepository.save(
                AccessRuleFactory.sampleExcludedVideoTypesAccessRule(
                    name = "BadVideos",
                    videoTypes = listOf(VideoType.STOCK, VideoType.NEWS)
                )
            )

            mvc.perform(
                    get("/v1/access-rules")
                        .asUserWithRoles("access-rules-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.accessRules", hasSize<Any>(1)))
                .andExpect(jsonPath("$._embedded.accessRules[0].type", equalTo("ExcludedVideoTypes")))
                .andExpect(jsonPath("$._embedded.accessRules[0].name", equalTo(accessRule.name)))
                .andExpect(jsonPath("$._embedded.accessRules[0].videoTypes", hasSize<Int>(2)))
                .andExpect(jsonPath("$._embedded.accessRules[0].videoTypes[*]", containsInAnyOrder("STOCK", "NEWS")))
                .andExpect(
                    jsonPath(
                        "$._embedded.accessRules[0]._links.self.href",
                        endsWith("/v1/access-rules/${accessRule.id.value}")
                    )
                )
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/access-rules{?name}")))
        }

        @Test
        fun `can fetch ExcludedContentPartners access rules`() {
            val accessRule = accessRuleRepository.save(
                AccessRuleFactory.sampleExcludedContentPartnersAccessRule(
                    name = "SomeBadCPs",
                    contentPartnerIds = listOf(ContentPartnerId("A"), ContentPartnerId("B"))
                )
            )

            mvc.perform(
                    get("/v1/access-rules")
                        .asUserWithRoles("access-rules-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.accessRules", hasSize<Any>(1)))
                .andExpect(jsonPath("$._embedded.accessRules[0].type", equalTo("ExcludedContentPartners")))
                .andExpect(jsonPath("$._embedded.accessRules[0].name", equalTo(accessRule.name)))
                .andExpect(jsonPath("$._embedded.accessRules[0].contentPartnerIds", hasSize<Int>(2)))
                .andExpect(jsonPath("$._embedded.accessRules[0].contentPartnerIds[*]", containsInAnyOrder("A", "B")))
                .andExpect(
                    jsonPath(
                        "$._embedded.accessRules[0]._links.self.href",
                        endsWith("/v1/access-rules/${accessRule.id.value}")
                    )
                )
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/access-rules{?name}")))
        }

        @Test
        fun `can fetch IncludedDistributionMethods access rules`() {
            val accessRule = accessRuleRepository.save(
                AccessRuleFactory.sampleIncludedDistributionMethodAccessRule(
                    name = "Stream only",
                    distributionMethods = setOf(DistributionMethod.STREAM)
                )
            )

            mvc.perform(
                    get("/v1/access-rules")
                        .asUserWithRoles("access-rules-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.accessRules", hasSize<Any>(1)))
                .andExpect(jsonPath("$._embedded.accessRules[0].type", equalTo("IncludedDistributionMethods")))
                .andExpect(jsonPath("$._embedded.accessRules[0].name", equalTo(accessRule.name)))
                .andExpect(jsonPath("$._embedded.accessRules[0].distributionMethods", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.accessRules[0].distributionMethods[*]", containsInAnyOrder("STREAM")))
                .andExpect(
                    jsonPath(
                        "$._embedded.accessRules[0]._links.self.href",
                        endsWith("/v1/access-rules/${accessRule.id.value}")
                    )
                )
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/access-rules{?name}")))
        }
    }
}
