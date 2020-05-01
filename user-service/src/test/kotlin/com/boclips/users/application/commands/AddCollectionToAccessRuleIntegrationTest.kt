package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.AccessRuleNotFoundException
import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AddCollectionToAccessRuleIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `adds collection to access rule and is idempotent`() {
        val existingId = CollectionId()
        val accessRule = accessRuleRepository.save(
            AccessRule.IncludedCollections(
                id = AccessRuleId(),
                name = "whatever",
                collectionIds = listOf(existingId)
            )
        )

        val newId = CollectionId()
        addCollectionToAccessRule(accessRuleId = accessRule.id, collectionId = newId)
        addCollectionToAccessRule(accessRuleId = accessRule.id, collectionId = newId)

        val updatedAccessRule = accessRuleRepository.findById(accessRule.id) as AccessRule.IncludedCollections

        assertThat(updatedAccessRule.collectionIds).containsOnly(existingId, newId)
    }

    @Test
    fun `throws AccessRuleNotFoundException when access rule is not found`() {
        assertThrows<AccessRuleNotFoundException> {
            addCollectionToAccessRule(
                accessRuleId = AccessRuleId(),
                collectionId = CollectionId()
            )
        }
    }
}
