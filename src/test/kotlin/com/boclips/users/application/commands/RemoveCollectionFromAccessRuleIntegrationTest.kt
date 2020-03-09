package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.AccessRuleNotFoundException
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class RemoveCollectionFromAccessRuleIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var removeCollectionFromAccessRule: RemoveCollectionFromAccessRule

    @Test
    fun `can remove a collection from a access rule`() {
        val existingId = CollectionId("some-existing-id")
        val accessRule = selectedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
            "whatever",
            listOf(existingId)
        )

        removeCollectionFromAccessRule(accessRuleId = accessRule.id, collectionId = existingId)

        val updatedAccessRule = accessRuleRepository.findById(accessRule.id) as AccessRule.IncludedCollections

        assertThat(updatedAccessRule.collectionIds).isEmpty()
    }

    @Test
    fun `throws a not found exception when access rule is not found`() {
        assertThrows<AccessRuleNotFoundException> {
            removeCollectionFromAccessRule(
                accessRuleId = AccessRuleId("does not exist"),
                collectionId = CollectionId("anything")
            )
        }
    }

    @Test
    fun `does not fail when collection to remove is missing (is idempotent)`() {
        val accessRule = selectedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
            "whatever",
            emptyList()
        )

        removeCollectionFromAccessRule(accessRuleId = accessRule.id, collectionId = CollectionId("some-id"))

        val updatedAccessRule = accessRuleRepository.findById(accessRule.id) as AccessRule.IncludedCollections

        assertThat(updatedAccessRule.collectionIds).isEmpty()
    }
}
