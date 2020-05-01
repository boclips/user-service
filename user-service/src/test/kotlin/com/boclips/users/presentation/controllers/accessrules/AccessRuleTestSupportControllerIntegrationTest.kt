package com.boclips.users.presentation.controllers.accessrules

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.service.UniqueId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AccessRuleTestSupportControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class CreatingAccessRules {
        @Test
        fun `returns a 403 response when user does not have an INSERT_ACCESS_RULES role`() {
            mvc.perform(
                post("/v1/access-rules")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ }")
                    .asUser("cant-create-contracts@hacker.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `creates a IncludedCollections access rule and returns it's location`() {
            mvc.perform(
                post("/v1/access-rules")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "type": "IncludedCollections",
                            "name": "Collections contract creation test",
                            "collectionIds": ["A", "B", "C"]
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("contract-creator@hacker.com", UserRoles.INSERT_ACCESS_RULES)
            )
                .andExpect(status().isCreated)
                .andExpect(header().string("Location", containsString("/v1/access-rules/")))
        }

        @Test
        fun `creates a IncludedVideos access rule and returns it's location`() {
            mvc.perform(
                post("/v1/access-rules")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "type": "IncludedVideos",
                            "name": "Videos contract creation test",
                            "videoIds": ["A", "B", "C"]
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("contract-creator@hacker.com", UserRoles.INSERT_ACCESS_RULES)
            )
                .andExpect(status().isCreated)
                .andExpect(header().string("Location", containsString("/v1/access-rules/")))
        }

        @Test
        fun `creates a ExcludedVideoTypes access rule and returns it's location`() {
            mvc.perform(
                post("/v1/access-rules")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "type": "ExcludedVideoTypes",
                            "name": "Videos contract creation test",
                            "videoTypes": ["NEWS"]
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("contract-creator@hacker.com", UserRoles.INSERT_ACCESS_RULES)
            )
                .andExpect(status().isCreated)
                .andExpect(header().string("Location", containsString("/v1/access-rules/")))
        }

        @Test
        fun `returns a 400 response when IncludedCollections payload is invalid`() {
            mvc.perform(
                post("/v1/access-rules")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "type": "IncludedCollections"
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("contract-creator@hacker.com", UserRoles.INSERT_ACCESS_RULES)
            )
                .andExpect(status().isBadRequest)
                .andExpectApiErrorPayload()
        }

        @Test
        fun `returns a 400 response when access rule type is not provided`() {
            mvc.perform(
                post("/v1/access-rules")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "name": "Contract type is not there...",
                            "collectionIds": ["A", "B", "C"]
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("contract-creator@hacker.com", UserRoles.INSERT_ACCESS_RULES)
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `returns 400 when incorrect video type is provided`() {
            mvc.perform(
                post("/v1/access-rules")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "type": "ExcludedVideoTypes",
                            "name": "Videos contract creation test",
                            "videoTypes": ["bad", "very bad"]
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("contract-creator@hacker.com", UserRoles.INSERT_ACCESS_RULES)
            )
                .andExpect(status().isBadRequest)
                .andExpectApiErrorPayload()
        }

        @Test
        fun `returns a 409 response when a access rule with given name already exists`() {
            val accessRule = "Super contract"
            accessRuleRepository.save(
                AccessRule.IncludedCollections(id = AccessRuleId(), name = accessRule, collectionIds = listOf(CollectionId("A"))
            ))

            mvc.perform(
                post("/v1/access-rules")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "type": "IncludedCollections",
                            "name": "$accessRule",
                            "collectionIds": ["A"]
                        }
                    """.trimIndent()
                    )
                    .asUserWithRoles("contract-creator@hacker.com", UserRoles.INSERT_ACCESS_RULES)
            )
                .andExpect(status().isConflict)
        }
    }

    @Nested
    inner class FetchingAccessRules {
        @Test
        fun `returns a 403 response when caller does not have a VIEW_ACCESS_RULES role`() {
            mvc.perform(
                get("/v1/access-rules/some-contract-id").asUser("cant-view-contracts@hacker.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `returns requested access rule`() {
            val accessRuleName = "Super contract"
            val accessRule = accessRuleRepository.save(AccessRule.IncludedCollections(id = AccessRuleId(), name = accessRuleName, collectionIds = listOf(CollectionId("A"))))

            mvc.perform(
                get("/v1/access-rules/${accessRule.id.value}")
                    .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.type", equalTo("IncludedCollections")))
                .andExpect(jsonPath("$.name", equalTo(accessRuleName)))
                .andExpect(jsonPath("$.collectionIds", hasSize<Int>(1)))
                .andExpect(jsonPath("$.collectionIds[0]", equalTo("A")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/access-rules/${accessRule.id.value}")))
        }

        @Test
        fun `returns a 404 response when given access rule is not found`() {
            mvc.perform(
                get("/v1/access-rules/${UniqueId()}")
                    .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
            )
                .andExpect(status().isNotFound)
        }
    }
}
