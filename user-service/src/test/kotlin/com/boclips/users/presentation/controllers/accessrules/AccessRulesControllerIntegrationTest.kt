package com.boclips.users.presentation.controllers.accessrules

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.model.access.ChannelId
import com.boclips.users.domain.model.access.DistributionMethod
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.domain.model.access.VideoType
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.AccessRuleFactory
import com.boclips.users.testsupport.factories.ContentPackageFactory
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
            val accessRule = AccessRule.IncludedCollections(id = AccessRuleId(), name = accessRuleName, collectionIds = listOf(CollectionId("A")));
            contentPackageRepository.save(ContentPackageFactory.sample(accessRules = listOf(accessRule)))

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
        }

        @Test
        fun `can fetch all access rules in the system when name query parameter is not provided`() {
            val firstAccessRuleName = "first"
            val firstAccessRule = AccessRule.IncludedCollections(
                id = AccessRuleId(),
                name = firstAccessRuleName,
                collectionIds = listOf(CollectionId("A"))
            );
            contentPackageRepository.save(ContentPackageFactory.sample(accessRules = listOf(firstAccessRule)))

            val secondAccessRuleName = "second"
            val secondAccessRule = AccessRule.IncludedCollections(
                id = AccessRuleId(),
                name = secondAccessRuleName,
                collectionIds = listOf(CollectionId("B"))
            );
            contentPackageRepository.save(ContentPackageFactory.sample(accessRules = listOf(secondAccessRule)))


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

                .andExpect(jsonPath("$._embedded.accessRules[1].type", equalTo("IncludedCollections")))
                .andExpect(jsonPath("$._embedded.accessRules[1].name", equalTo(secondAccessRuleName)))
                .andExpect(jsonPath("$._embedded.accessRules[1].collectionIds", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.accessRules[1].collectionIds[0]", equalTo("B")))
        }

        @Test
        fun `can fetch ExcludedVideos access rules`() {
            val accessRule = AccessRule.ExcludedVideos(
                id = AccessRuleId(),
                name = "BadVideos",
                videoIds = listOf(VideoId("A"), VideoId("B"))
            );
            contentPackageRepository.save(ContentPackageFactory.sample(accessRules = listOf(accessRule)))

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
        }

        @Test
        fun `can fetch ExcludedVideoTypes access rules`() {
            val accessRule = AccessRuleFactory.sampleExcludedVideoTypesAccessRule(
                name = "BadVideos",
                videoTypes = listOf(VideoType.STOCK, VideoType.NEWS)
            )

            contentPackageRepository.save(ContentPackageFactory.sample(accessRules = listOf(accessRule)))

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
        }

        @Test
        fun `can fetch ExcludedContentPartners access rules`() {
            val accessRule = AccessRuleFactory.sampleExcludedContentPartnersAccessRule(
                name = "SomeBadCPs",
                channelIds = listOf(ChannelId("A"), ChannelId("B"))
            )
            contentPackageRepository.save(ContentPackageFactory.sample(accessRules = listOf(accessRule)))

            mvc.perform(
                    get("/v1/access-rules")
                        .asUserWithRoles("access-rules-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.accessRules", hasSize<Any>(1)))
                .andExpect(jsonPath("$._embedded.accessRules[0].type", equalTo("ExcludedChannels")))
                .andExpect(jsonPath("$._embedded.accessRules[0].name", equalTo(accessRule.name)))
                .andExpect(jsonPath("$._embedded.accessRules[0].channelIds", hasSize<Int>(2)))
                .andExpect(jsonPath("$._embedded.accessRules[0].channelIds[*]", containsInAnyOrder("A", "B")))
        }

        @Test
        fun `can fetch IncludedDistributionMethods access rules`() {
            val accessRule = AccessRuleFactory.sampleIncludedDistributionMethodAccessRule(
                name = "Stream only",
                distributionMethods = setOf(DistributionMethod.STREAM)
            )
            contentPackageRepository.save(ContentPackageFactory.sample(accessRules = listOf(accessRule)))

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
        }
    }
}
