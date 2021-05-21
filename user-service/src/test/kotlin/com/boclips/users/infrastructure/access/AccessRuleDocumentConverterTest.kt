package com.boclips.users.infrastructure.access

import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.ChannelId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.model.access.DistributionMethod
import com.boclips.users.domain.model.access.PlaybackSource
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.domain.model.access.VideoType
import com.boclips.users.domain.model.access.VideoVoiceType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.Locale
import java.util.stream.Stream

class AccessRuleDocumentConverterTest {

    class AccessRuleProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?) = Stream.of(
            AccessRule.IncludedCollections(
                name = "included cols",
                collectionIds = listOf(
                    CollectionId("collection-1")
                )
            ),
            AccessRule.IncludedVideos(
                name = "included vids",
                videoIds = listOf(
                    VideoId("video-1")
                )
            ),
            AccessRule.ExcludedVideos(
                name = "excluded vids",
                videoIds = listOf(
                    VideoId("video-1")
                )
            ),
            AccessRule.ExcludedChannels(
                name = "excluded CPs",
                channelIds = listOf(
                    ChannelId("cp-1")
                )
            ),
            AccessRule.ExcludedVideoTypes(
                name = "excluded CPs",
                videoTypes = listOf(VideoType.STOCK, VideoType.NEWS, VideoType.INSTRUCTIONAL)
            ),
            AccessRule.IncludedVideoTypes(
                name = "excluded CPs",
                videoTypes = listOf(VideoType.STOCK, VideoType.NEWS, VideoType.INSTRUCTIONAL)
            ),
            AccessRule.IncludedChannels(
                name = "included channels",
                channelIds = listOf(
                    ChannelId("channel-1")
                )
            ),
            AccessRule.IncludedDistributionMethods(
                name = "included distr methods",
                distributionMethods = setOf(DistributionMethod.DOWNLOAD, DistributionMethod.STREAM)
            ),
            AccessRule.IncludedVideoVoiceTypes(
                name = "voice types",
                voiceTypes = listOf(
                    VideoVoiceType.UNKNOWN_VOICE,
                    VideoVoiceType.WITHOUT_VOICE,
                    VideoVoiceType.WITH_VOICE
                )
            ),
            AccessRule.ExcludedLanguages(
                name = "languages",
                languages = setOf(
                    Locale.JAPANESE,
                    Locale.FRENCH
                )
            ),
            AccessRule.ExcludedPlaybackSources(
                name = "languages",
                sources = setOf(
                    PlaybackSource.KALTURA
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

    @Test
    fun `throws an exception when excluded source is invalid`() {
        val invalidAccessRuleDocument = AccessRuleDocument(
            name = "languages",
            sources = setOf("invalid_source"),
            _class = "ExcludedPlaybackSources"
        )
        val thrown = assertThrows<IllegalArgumentException> {
            AccessRuleDocumentConverter.fromDocument(invalidAccessRuleDocument)
        }

        assertThat(thrown).isExactlyInstanceOf(IllegalArgumentException::class.java)
    }
}
