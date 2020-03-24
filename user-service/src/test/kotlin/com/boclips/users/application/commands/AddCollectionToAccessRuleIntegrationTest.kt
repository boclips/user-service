package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.AccessRuleNotFoundException
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AddCollectionToAccessRuleIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `adds collection to access rule and is idempotent`() {
        val existingId = CollectionId("some-existing-id")
        val accessRule = includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
            "whatever",
            listOf(existingId)
        )

        val newId = CollectionId("another-id")
        addCollectionToAccessRule(accessRuleId = accessRule.id, collectionId = newId)
        addCollectionToAccessRule(accessRuleId = accessRule.id, collectionId = newId)

        val updatedAccessRule = accessRuleRepository.findById(accessRule.id) as AccessRule.IncludedCollections

        assertThat(updatedAccessRule.collectionIds).containsOnly(existingId, newId)
    }

    @Test
    fun `throws AccessRuleNotFoundException when access rule is not found`() {
        assertThrows<AccessRuleNotFoundException> {
            addCollectionToAccessRule(
                accessRuleId = AccessRuleId("does not exist"),
                collectionId = CollectionId("another-id")
            )
        }
    }
}