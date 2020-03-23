package com.boclips.users.presentation.controllers.accessrules

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class IncludedContentAccessRuleControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class AddingCollections {
        @Test
        fun `returns a 403 response if caller does not have UPDATE_ACCESS_RULES role`() {
            val accessRuleId = "test-contract-id"
            val collectionId = "test-collection-id"

            mvc.perform(
                    put("/v1/included-content-access-rules/$accessRuleId/collections/$collectionId").asUser("no-roles@doh.com")
                )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `adds the collection to the contract`() {
            val accessRuleId = includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                "Some test contract",
                emptyList()
            ).id
            val collectionId = "test-collection-id"

            mvc.perform(
                    put("/v1/included-content-access-rules/${accessRuleId.value}/collections/$collectionId")
                        .asUserWithRoles(
                            "test@user.com",
                            UserRoles.UPDATE_ACCESS_RULES
                        )
                )
                .andExpect(status().isNoContent)

            val updatedAccessRule = accessRuleRepository.findById(accessRuleId) as AccessRule.IncludedCollections

            assertThat(updatedAccessRule.collectionIds).contains(CollectionId(collectionId))
        }

        @Test
        fun `returns a 404 response if access rule is not found`() {
            mvc.perform(
                    put("/v1/included-content-access-rules/does-not-exist/collections/collection-id")
                        .asUserWithRoles(
                            "test@user.com",
                            UserRoles.UPDATE_ACCESS_RULES
                        )
                )
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    inner class RemovingCollections {
        @Test
        fun `returns a 403 response if caller does not have UPDATE_ACCESS_RULES role`() {
            val accessRuleId = "test-contract-id"
            val collectionId = "test-collection-id"

            mvc.perform(
                    delete("/v1/included-content-access-rules/$accessRuleId/collections/$collectionId").asUser("no-roles@doh.com")
                )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `removes provided collection from a contract`() {
            val collectionId = "test-collection-id"
            val accessRuleId = includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                "Some test contract",
                listOf(CollectionId(collectionId))
            ).id

            mvc.perform(
                    delete("/v1/included-content-access-rules/${accessRuleId.value}/collections/$collectionId")
                        .asUserWithRoles(
                            "test@user.com",
                            UserRoles.UPDATE_ACCESS_RULES
                        )
                )
                .andExpect(status().isNoContent)

            val updatedAccessRule = accessRuleRepository.findById(accessRuleId) as AccessRule.IncludedCollections

            assertThat(updatedAccessRule.collectionIds).isEmpty()
        }

        @Test
        fun `returns 404 when access rule does not exist`() {
            mvc.perform(
                    delete("/v1/included-content-access-rules/does-not-exist/collections/collection-id")
                        .asUserWithRoles(
                            "test@user.com",
                            UserRoles.UPDATE_ACCESS_RULES
                        )
                )
                .andExpect(status().isNotFound)
        }
    }
}
