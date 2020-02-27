package com.boclips.users.infrastructure.accessrules

import com.boclips.users.domain.model.accessrules.CollectionId
import com.boclips.users.domain.model.accessrules.VideoId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoSelectedContentAccessRuleRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `saves a selected collections access rule`() {
        val accessRuleName = "Test selected content contract"
        val persistedAccessRule = selectedContentAccessRuleRepository.saveSelectedCollectionsAccessRule(
            name = accessRuleName,
            collectionIds = listOf(CollectionId("A"), CollectionId("B"), CollectionId("C"))
        )

        assertThat(persistedAccessRule.id).isNotNull
        assertThat(persistedAccessRule.name).isEqualTo(accessRuleName)
        assertThat(persistedAccessRule.collectionIds).containsOnly(
            CollectionId("A"),
            CollectionId("B"),
            CollectionId("C")
        )
    }

    @Test
    fun `saves a selected videos access rule`() {
        val accessRuleName = "Test selected content contract"
        val persistedAccessRule = selectedContentAccessRuleRepository.saveSelectedVideosAccessRule(
            name = accessRuleName,
            videoIds = listOf(VideoId("A"), VideoId("B"), VideoId("C"))
        )

        assertThat(persistedAccessRule.id).isNotNull
        assertThat(persistedAccessRule.name).isEqualTo(accessRuleName)
        assertThat(persistedAccessRule.videoIds).containsOnly(VideoId("A"), VideoId("B"), VideoId("C"))
    }
}