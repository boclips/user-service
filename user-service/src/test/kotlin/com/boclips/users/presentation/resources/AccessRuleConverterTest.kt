package com.boclips.users.presentation.resources

import com.boclips.users.api.response.accessrule.AccessRuleResource
import com.boclips.users.domain.model.access.ChannelId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.presentation.converters.AccessRuleConverter
import com.boclips.users.presentation.hateoas.AccessRuleLinkBuilder
import com.boclips.users.presentation.hateoas.IncludedAccessRuleLinkBuilder
import com.boclips.users.testsupport.factories.AccessRuleFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AccessRuleConverterTest {
    @Test
    fun `converts included collections access rule`() {
        val accessRule = AccessRuleFactory.sampleIncludedCollectionsAccessRule(
            collectionIds = listOf(CollectionId("A"), CollectionId("B"))
        )

        val resource = converter.toResource(accessRule) as AccessRuleResource.IncludedCollections

        assertThat(resource.name).isEqualTo(accessRule.name)
        assertThat(resource.collectionIds).containsExactlyInAnyOrder("A", "B")
    }

    @Test
    fun `converts included videos access rule`() {
        val accessRule = AccessRuleFactory.sampleIncludedVideosAccessRule(
            videoIds = listOf(VideoId("A"), VideoId("B"))
        )

        val resource = converter.toResource(accessRule) as AccessRuleResource.IncludedVideos

        assertThat(resource.name).isEqualTo(accessRule.name)
        assertThat(resource.videoIds).containsExactlyInAnyOrder("A", "B")
    }

    @Test
    fun `converts included channel access rule`() {
        val accessRule = AccessRuleFactory.sampleIncludedChannelsAccessRule(channelIds = listOf(ChannelId("A")))

        val resource = converter.toResource(accessRule) as AccessRuleResource.IncludedChannels

        assertThat(resource.channelIds).containsExactly("A")
    }

    private val converter =
        AccessRuleConverter(
            AccessRuleLinkBuilder(),
            IncludedAccessRuleLinkBuilder()
        )
}
