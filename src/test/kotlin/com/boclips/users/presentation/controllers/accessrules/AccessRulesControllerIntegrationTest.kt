package com.boclips.users.presentation.controllers.accessrules

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
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
                    .asUser("cant-view-contracts@hacker.com")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `returns given access rule on the list when the name matches`() {
            val accessRuleName = "Super contract"
            val accessRule = includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                accessRuleName,
                listOf(CollectionId("A"))
            )

            mvc.perform(
                get(
                    UriComponentsBuilder.fromUriString("/v1/access-rules")
                        .queryParam("name", accessRuleName)
                        .build()
                        .toUri()
                )
                    .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.accessRules", hasSize<Any>(1)))
                .andExpect(jsonPath("$._embedded.accessRules[0].type", equalTo("SelectedCollections")))
                .andExpect(jsonPath("$._embedded.accessRules[0].type", equalTo("SelectedCollections")))
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
            includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                "Super contract",
                listOf(CollectionId("A"))
            )

            mvc.perform(
                get(
                    UriComponentsBuilder.fromUriString("/v1/access-rules")
                        .queryParam("name", "this does not exist")
                        .build()
                        .toUri()
                )
                    .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.accessRules", hasSize<Any>(0)))
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/access-rules?name=this%20does%20not%20exist")))
        }

        @Test
        fun `returns an empty list when lookup is done with a blank parameter`() {
            includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                "Super contract",
                listOf(CollectionId("A"))
            )

            mvc.perform(
                get(
                    UriComponentsBuilder.fromUriString("/v1/access-rules")
                        .queryParam("name", "")
                        .build()
                        .toUri()
                )
                    .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.accessRules", hasSize<Any>(0)))
                .andExpect(jsonPath("$._links.self.href", endsWith("/v1/access-rules?name=")))
        }

        @Test
        fun `returns all access rules in the system when name query parameter is not provided`() {
            val firstAccessRuleName = "first"
            val firstAccessRule = includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                firstAccessRuleName,
                listOf(CollectionId("A"))
            )

            val secondAccessRuleName = "second"
            val secondAccessRule = includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                secondAccessRuleName,
                listOf(CollectionId("B"))
            )

            mvc.perform(
                get("/v1/access-rules")
                    .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_ACCESS_RULES)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.accessRules", hasSize<Any>(2)))
                .andExpect(jsonPath("$._embedded.accessRules[0].type", equalTo("SelectedCollections")))
                .andExpect(jsonPath("$._embedded.accessRules[0].name", equalTo(firstAccessRuleName)))
                .andExpect(jsonPath("$._embedded.accessRules[0].collectionIds", hasSize<Int>(1)))
                .andExpect(jsonPath("$._embedded.accessRules[0].collectionIds[0]", equalTo("A")))
                .andExpect(
                    jsonPath(
                        "$._embedded.accessRules[0]._links.self.href",
                        endsWith("/v1/access-rules/${firstAccessRule.id.value}")
                    )
                )
                .andExpect(jsonPath("$._embedded.accessRules[1].type", equalTo("SelectedCollections")))
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
    }
}
