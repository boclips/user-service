package com.boclips.users.infrastructure.accessrules

import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoIncludedContentAccessRuleRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `saves a selected collections access rule`() {
        val accessRuleName = "Test selected content contract"
        val persistedAccessRule = selectedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
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
        val persistedAccessRule = selectedContentAccessRuleRepository.saveIncludedVideosAccessRule(
            name = accessRuleName,
            videoIds = listOf(VideoId("A"), VideoId("B"), VideoId("C"))
        )

        assertThat(persistedAccessRule.id).isNotNull
        assertThat(persistedAccessRule.name).isEqualTo(accessRuleName)
        assertThat(persistedAccessRule.videoIds).containsOnly(VideoId("A"), VideoId("B"), VideoId("C"))
    }
}
