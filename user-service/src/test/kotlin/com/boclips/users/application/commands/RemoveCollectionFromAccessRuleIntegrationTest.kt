package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.AccessRuleNotFoundException
import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

@Ignore
class RemoveCollectionFromAccessRuleIntegrationTest : AbstractSpringIntegrationTest() {
//    @Autowired
//    lateinit var removeCollectionFromAccessRule: RemoveCollectionFromAccessRule
//
//    @Test
//    fun `can remove a collection from a access rule`() {
//        val existingId = CollectionId()
//        val accessRule = AccessRule.IncludedCollections(
//            id = AccessRuleId(),
//            name = "whatever",
//            collectionIds = listOf(existingId)
//        )
//
//        removeCollectionFromAccessRule(accessRuleId = accessRule.id, collectionId = existingId)
//
//        val updatedAccessRule = accessRuleRepository.findById(accessRule.id) as AccessRule.IncludedCollections
//
//        assertThat(updatedAccessRule.collectionIds).isEmpty()
//    }
//
//    @Test
//    fun `throws a not found exception when access rule is not found`() {
//        assertThrows<AccessRuleNotFoundException> {
//            removeCollectionFromAccessRule(
//                accessRuleId = AccessRuleId(),
//                collectionId = CollectionId("anything")
//            )
//        }
//    }
//
//    @Test
//    fun `does not fail when collection to remove is missing (is idempotent)`() {
//        val accessRule = accessRuleRepository.save(
//            AccessRule.IncludedCollections(
//                id = AccessRuleId(), name = "whatever",
//                collectionIds = emptyList()
//            )
//        )
//
//        removeCollectionFromAccessRule(accessRuleId = accessRule.id, collectionId = CollectionId("some-id"))
//
//        val updatedAccessRule = accessRuleRepository.findById(accessRule.id) as AccessRule.IncludedCollections
//
//        assertThat(updatedAccessRule.collectionIds).isEmpty()
//    }
}
