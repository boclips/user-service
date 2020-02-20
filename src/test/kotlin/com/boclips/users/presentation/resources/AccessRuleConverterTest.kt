package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.accessrules.CollectionId
import com.boclips.users.domain.model.accessrules.VideoId
import com.boclips.users.presentation.hateoas.AccessRuleLinkBuilder
import com.boclips.users.presentation.hateoas.SelectedAccessRuleLinkBuilder
import com.boclips.users.presentation.resources.converters.AccessRuleConverter
import com.boclips.users.testsupport.factories.AccessRuleFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AccessRuleConverterTest {
    @Test
    fun `converts selected collections access rule`() {
        val accessRule = AccessRuleFactory.sampleSelectedCollectionsAccessRule(
            collectionIds = listOf(CollectionId("A"), CollectionId("B"))
        )

        val resource = converter.toResource(accessRule) as AccessRuleResource.SelectedCollections

        assertThat(resource.name).isEqualTo(accessRule.name)
        assertThat(resource.collectionIds).containsExactlyInAnyOrder("A", "B")
    }

    @Test
    fun `converts selected videos access rule`() {
        val accessRule = AccessRuleFactory.sampleSelectedVideosAccessRule(
            videoIds = listOf(VideoId("A"), VideoId("B"))
        )

        val resource = converter.toResource(accessRule) as AccessRuleResource.SelectedVideos

        assertThat(resource.name).isEqualTo(accessRule.name)
        assertThat(resource.videoIds).containsExactlyInAnyOrder("A", "B")
    }

    private val converter = AccessRuleConverter(
        AccessRuleLinkBuilder(),
        SelectedAccessRuleLinkBuilder()
    )
}
