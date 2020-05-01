package com.boclips.users.infrastructure.access

import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MongoAccessRuleRepositoryTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class FindById {
        @Test
        fun `fetches a collection access rule by id and deserializes it to a correct class`() {
            val persistedAccessRule = accessRuleRepository.save(
                AccessRule.IncludedCollections(
                    id = AccessRuleId(),
                    name = "Test included content contract",
                    collectionIds = listOf(CollectionId("A"), CollectionId("B"), CollectionId("C"))
                )
            )

            val fetchedAccessRule = accessRuleRepository.findById(persistedAccessRule.id)

            assertThat(fetchedAccessRule).isEqualTo(persistedAccessRule)
        }

        @Test
        fun `fetches a video access rule by id and deserializes it to the correct class`() {
            val persistedAccessRule = accessRuleRepository.save(
                AccessRule.IncludedVideos(
                    id = AccessRuleId(),
                    name = "Test included content contract",
                    videoIds = listOf(VideoId("A"), VideoId("B"), VideoId("C"))
                )
            )

            val fetchedAccessRule = accessRuleRepository.findById(persistedAccessRule.id)

            assertThat(fetchedAccessRule).isEqualTo(persistedAccessRule)
        }

        @Test
        fun `returns null if access rule is not found by id`() {
            assertThat(accessRuleRepository.findById(AccessRuleId())).isNull()
        }
    }

    @Nested
    inner class FindAllByName {
        @Test
        fun `looks up access rules by name and deserializes them to a correct class`() {
            val accessRuleName = "Name Test"
            val persistedAccessRule = accessRuleRepository.save(
                AccessRule.IncludedCollections(
                    id = AccessRuleId(),
                    name = accessRuleName,
                    collectionIds = listOf(CollectionId("A"), CollectionId("B"), CollectionId("C"))
                )
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
            val firstCollectionAccessRule = accessRuleRepository.save(
                AccessRule.IncludedCollections(
                    id = AccessRuleId(),
                    name = "Hey",
                    collectionIds = emptyList()
                )
            )
            val secondCollectionAccessRule = accessRuleRepository.save(
                AccessRule.IncludedCollections(
                    id = AccessRuleId(),
                    name = "Ho",
                    collectionIds = emptyList()
                )
            )
            val firstVideoAccessRule =
                accessRuleRepository.save(
                    AccessRule.IncludedVideos(
                        id = AccessRuleId(),
                        name = "Yo",
                        videoIds = emptyList()
                    )
                )

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
            val firstCollectionAccessRule = accessRuleRepository.save(
                AccessRule.IncludedCollections(
                    id = AccessRuleId(),
                    name = "Hey",
                    collectionIds = emptyList()
                )
            )

            val secondCollectionAccessRule = accessRuleRepository.save(
                AccessRule.IncludedCollections(
                    id = AccessRuleId(),
                    name = "Ho",
                    collectionIds = emptyList()
                )
            )

            accessRuleRepository.save(
                AccessRule.IncludedCollections(
                    id = AccessRuleId(),
                    name = "Hola",
                    collectionIds = emptyList()
                )
            )

            val accessRules =
                accessRuleRepository.findByIds(listOf(firstCollectionAccessRule.id, secondCollectionAccessRule.id))
            assertThat(accessRules).containsExactlyInAnyOrder(firstCollectionAccessRule, secondCollectionAccessRule)
        }
    }
}
