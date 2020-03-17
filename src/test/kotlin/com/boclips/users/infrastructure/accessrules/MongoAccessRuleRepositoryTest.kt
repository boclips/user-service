package com.boclips.users.infrastructure.accessrules

import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MongoAccessRuleRepositoryTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class FindById {
        @Test
        fun `fetches a collection access rule by id and deserializes it to a correct class`() {
            val persistedAccessRule = includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                name = "Test included content contract",
                collectionIds = listOf(CollectionId("A"), CollectionId("B"), CollectionId("C"))
            )

            val fetchedAccessRule = accessRuleRepository.findById(persistedAccessRule.id)

            assertThat(fetchedAccessRule).isEqualTo(persistedAccessRule)
        }

        @Test
        fun `fetches a video access rule by id and deserializes it to the correct class`() {
            val persistedAccessRule = includedContentAccessRuleRepository.saveIncludedVideosAccessRule(
                name = "Test included content contract",
                videoIds = listOf(VideoId("A"), VideoId("B"), VideoId("C"))
            )

            val fetchedAccessRule = accessRuleRepository.findById(persistedAccessRule.id)

            assertThat(fetchedAccessRule).isEqualTo(persistedAccessRule)
        }

        @Test
        fun `returns null if access rule is not found by id`() {
            assertThat(accessRuleRepository.findById(AccessRuleId("this does not exist"))).isNull()
        }
    }

    @Nested
    inner class FindAllByName {
        @Test
        fun `looks up access rules by name and deserializes them to a correct class`() {
            val accessRuleName = "Name Test"
            val persistedAccessRule = includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                name = accessRuleName,
                collectionIds = listOf(CollectionId("A"), CollectionId("B"), CollectionId("C"))
            )

            val foundAccessRule = accessRuleRepository.findAllByName(accessRuleName)

            assertThat(foundAccessRule).containsOnly(persistedAccessRule)
        }

        @Test
        fun `returns an empty list if no access rule is found by name`() {
            assertThat(accessRuleRepository.findAllByName("this does not exist")).isEmpty()
        }
    }

    @Nested
    inner class FindAll {
        @Test
        fun `returns all access rules`() {
            val firstCollectionAccessRule = includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                name = "Hey",
                collectionIds = emptyList()
            )
            val secondCollectionAccessRule = includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                name = "Ho",
                collectionIds = emptyList()
            )
            val firstVideoAccessRule =
                includedContentAccessRuleRepository.saveIncludedVideosAccessRule(name = "Yo", videoIds = emptyList())

            val allAccessRules = accessRuleRepository.findAll()

            assertThat(allAccessRules).hasSize(3)
            assertThat(allAccessRules).containsExactlyInAnyOrder(
                firstCollectionAccessRule,
                secondCollectionAccessRule,
                firstVideoAccessRule
            )
        }

        @Test
        fun `returns an empty list if no access rules are found`() {
            assertThat(accessRuleRepository.findAll()).isEmpty()
        }
    }

    @Nested
    inner class FindByIds {
        @Test
        fun `returns all access rules matching ids`() {
            val firstCollectionAccessRule = includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                name = "Hey",
                collectionIds = emptyList()
            )

            val secondCollectionAccessRule = includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                name = "Ho",
                collectionIds = emptyList()
            )

            includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                name = "Hola",
                collectionIds = emptyList()
            )

            val accessRules = accessRuleRepository.findByIds(listOf(firstCollectionAccessRule.id, secondCollectionAccessRule.id))
            assertThat(accessRules).containsExactlyInAnyOrder(firstCollectionAccessRule, secondCollectionAccessRule)
        }
    }
}
