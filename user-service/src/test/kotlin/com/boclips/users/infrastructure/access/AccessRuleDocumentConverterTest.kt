package com.boclips.users.infrastructure.access

import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.ChannelId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.model.access.DistributionMethod
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.domain.model.access.VideoType
import com.boclips.users.domain.model.access.VideoVoiceType
import com.boclips.users.domain.service.UniqueId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

class AccessRuleDocumentConverterTest {

    class AccessRuleProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?) = Stream.of(
            AccessRule.IncludedCollections(
                id = AccessRuleId(UniqueId()),
                name = "included cols",
                collectionIds = listOf(
                    CollectionId("collection-1")
                )
            ),
            AccessRule.IncludedVideos(
                id = AccessRuleId(UniqueId()),
                name = "included vids",
                videoIds = listOf(
                    VideoId("video-1")
                )
            ),
            AccessRule.ExcludedVideos(
                id = AccessRuleId(UniqueId()),
                name = "excluded vids",
                videoIds = listOf(
                    VideoId("video-1")
                )
            ),
            AccessRule.ExcludedChannels(
                id = AccessRuleId(UniqueId()),
                name = "excluded CPs",
                channelIds = listOf(
                    ChannelId("cp-1")
                )
            ),
            AccessRule.ExcludedVideoTypes(
                id = AccessRuleId(UniqueId()),
                name = "excluded CPs",
                videoTypes = listOf(VideoType.STOCK, VideoType.NEWS, VideoType.INSTRUCTIONAL)
            ),
            AccessRule.IncludedVideoTypes(
                id = AccessRuleId(UniqueId()),
                name = "excluded CPs",
                videoTypes = listOf(VideoType.STOCK, VideoType.NEWS, VideoType.INSTRUCTIONAL)
            ),
            AccessRule.IncludedChannels(
                id = AccessRuleId(UniqueId()),
                name = "included channels",
                channelIds = listOf(
                    ChannelId("channel-1")
                )
            ),
            AccessRule.IncludedDistributionMethods(
                id = AccessRuleId(UniqueId()),
                name = "included distr methods",
                distributionMethods = setOf(DistributionMethod.DOWNLOAD, DistributionMethod.STREAM)
            ),
            AccessRule.IncludedVideoVoiceTypes(
                id = AccessRuleId(UniqueId()),
                name = "voice types",
                voiceTypes = listOf(
                    VideoVoiceType.UNKNOWN_VOICE,
                    VideoVoiceType.WITHOUT_VOICE,
                    VideoVoiceType.WITH_VOICE
                )
            )
        ).map { accessRule -> Arguments.of(accessRule) }
    }

    @ParameterizedTest
    @ArgumentsSource(AccessRuleProvider::class)
    fun `convert an access rule to document and back`(originalRule: AccessRule) {
        val retrievedRule = originalRule
            .let(AccessRuleDocumentConverter::toDocument)
            .let(AccessRuleDocumentConverter::fromDocument)

        assertThat(retrievedRule).isEqualTo(originalRule)
    }
}
