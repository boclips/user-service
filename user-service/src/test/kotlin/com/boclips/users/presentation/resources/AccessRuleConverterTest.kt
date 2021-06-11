package com.boclips.users.presentation.resources

import com.boclips.users.api.response.accessrule.AccessRuleResource
import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.ChannelId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.model.access.DistributionMethod
import com.boclips.users.domain.model.access.PlaybackSource
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.domain.model.access.VideoType
import com.boclips.users.domain.model.access.VideoVoiceType
import com.boclips.users.presentation.converters.AccessRuleConverter
import com.boclips.users.testsupport.factories.AccessRuleFactory
import com.boclips.users.testsupport.factories.AccessRuleRequestFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Locale

class AccessRuleConverterTest {

    @Nested
    inner class ToResource {
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
        fun `converts included video types access rule`() {
            val accessRule = AccessRuleFactory.sampleIncludedVideoTypesAccessRule(
                videoTypes = listOf(VideoType.INSTRUCTIONAL, VideoType.NEWS, VideoType.STOCK)
            )

            val resource = converter.toResource(accessRule) as AccessRuleResource.IncludedVideoTypes

            assertThat(resource.name).isEqualTo(accessRule.name)
            assertThat(resource.videoTypes).containsExactlyInAnyOrder("INSTRUCTIONAL", "NEWS", "STOCK")
        }

        @Test
        fun `converts included channel access rule`() {
            val accessRule = AccessRuleFactory.sampleIncludedChannelsAccessRule(channelIds = listOf(ChannelId("A")))

            val resource = converter.toResource(accessRule) as AccessRuleResource.IncludedChannels

            assertThat(resource.channelIds).containsExactly("A")
        }

        @Test
        fun `converts included video voice access rule`() {
            val accessRule = AccessRuleFactory.sampleIncludedVideoVoiceTypeAccessRule(
                videoVoiceTypes = listOf(
                    VideoVoiceType.UNKNOWN_VOICE,
                    VideoVoiceType.WITHOUT_VOICE,
                    VideoVoiceType.WITH_VOICE
                )
            )

            val resource = converter.toResource(accessRule) as AccessRuleResource.IncludedVideoVoiceTypes

            assertThat(resource.voiceTypes).containsExactlyInAnyOrder("UNKNOWN_VOICE", "WITHOUT_VOICE", "WITH_VOICE")
        }

        @Test
        fun `converts excluded languages access rule`() {
            val accessRule = AccessRuleFactory.sampleExcludedLanguagesAccessRule(
                languages = setOf(Locale.FRENCH, Locale.ENGLISH)
            )

            val resource = converter.toResource(accessRule) as AccessRuleResource.ExcludedLanguages

            assertThat(resource.languages).containsExactlyInAnyOrder(Locale.FRENCH.toLanguageTag(), Locale.ENGLISH.toLanguageTag())
        }
    }

    @Nested
    inner class FromResource {
        @Test
        fun `converts included collections request to access rule`() {
            val accessRule = AccessRuleRequestFactory.sampleIncludedCollectionsAccessRuleRequest(
                name = "access-rule",
                collectionIds = listOf("A", "B")
            )
            val convertedAccessRule = converter.fromRequest(accessRule)

            assertThat(convertedAccessRule.name).isEqualTo(accessRule.name)
            assertThat(convertedAccessRule).isInstanceOf(AccessRule.IncludedCollections::class.java)
            assertThat((convertedAccessRule as AccessRule.IncludedCollections).collectionIds)
                .isEqualTo(listOf(CollectionId("A"), CollectionId("B")))
        }

        @Test
        fun `converts included videos request to access rule`() {
            val accessRule = AccessRuleRequestFactory.sampleIncludedVideosAccessRuleRequest(
                name = "access-rule",
                videoIds = listOf("A", "B")
            )
            val convertedAccessRule = converter.fromRequest(accessRule)

            assertThat(convertedAccessRule.name).isEqualTo(accessRule.name)
            assertThat(convertedAccessRule).isInstanceOf(AccessRule.IncludedVideos::class.java)
            assertThat((convertedAccessRule as AccessRule.IncludedVideos).videoIds)
                .isEqualTo(listOf(VideoId("A"), VideoId("B")))
        }

        @Test
        fun `converts excluded videos request to access rule`() {
            val accessRule = AccessRuleRequestFactory.sampleExcludedVideosAccessRuleRequest(
                name = "access-rule",
                videoIds = listOf("A", "B")
            )
            val convertedAccessRule = converter.fromRequest(accessRule)

            assertThat(convertedAccessRule.name).isEqualTo(accessRule.name)
            assertThat(convertedAccessRule).isInstanceOf(AccessRule.ExcludedVideos::class.java)
            assertThat((convertedAccessRule as AccessRule.ExcludedVideos).videoIds)
                .isEqualTo(listOf(VideoId("A"), VideoId("B")))
        }

        @Test
        fun `converts excluded video types request to access rule`() {
            val accessRule = AccessRuleRequestFactory.sampleExcludedVideoTypesAccessRuleRequest(
                name = "access-rule",
                videoTypes = listOf("STOCK", "INSTRUCTIONAL", "NEWS")
            )
            val convertedAccessRule = converter.fromRequest(accessRule)

            assertThat(convertedAccessRule.name).isEqualTo(accessRule.name)
            assertThat(convertedAccessRule).isInstanceOf(AccessRule.ExcludedVideoTypes::class.java)
            assertThat((convertedAccessRule as AccessRule.ExcludedVideoTypes).videoTypes)
                .isEqualTo(listOf(VideoType.STOCK, VideoType.INSTRUCTIONAL, VideoType.NEWS))
        }

        @Test
        fun `converts excluded channels request to access rule`() {
            val accessRule = AccessRuleRequestFactory.sampleExcludedChannelsAccessRuleRequest(
                name = "access-rule",
                channelIds = listOf("A", "B")
            )
            val convertedAccessRule = converter.fromRequest(accessRule)

            assertThat(convertedAccessRule.name).isEqualTo(accessRule.name)
            assertThat(convertedAccessRule).isInstanceOf(AccessRule.ExcludedChannels::class.java)
            assertThat((convertedAccessRule as AccessRule.ExcludedChannels).channelIds)
                .isEqualTo(listOf(ChannelId("A"), ChannelId("B")))
        }

        @Test
        fun `converts included channels request to access rule`() {
            val accessRule = AccessRuleRequestFactory.sampleIncludedChannelsAccessRuleRequest(
                name = "access-rule",
                channelIds = listOf("A", "B")
            )
            val convertedAccessRule = converter.fromRequest(accessRule)

            assertThat(convertedAccessRule.name).isEqualTo(accessRule.name)
            assertThat(convertedAccessRule).isInstanceOf(AccessRule.IncludedChannels::class.java)
            assertThat((convertedAccessRule as AccessRule.IncludedChannels).channelIds)
                .isEqualTo(listOf(ChannelId("A"), ChannelId("B")))
        }

        @Test
        fun `converts included distribution methods request to access rule`() {
            val accessRule = AccessRuleRequestFactory.sampleIncludedDistributionMethodsAccessRuleRequest(
                name = "access-rule",
                distributionMethods = listOf("STREAM", "DOWNLOAD")
            )
            val convertedAccessRule = converter.fromRequest(accessRule)

            assertThat(convertedAccessRule.name).isEqualTo(accessRule.name)
            assertThat(convertedAccessRule).isInstanceOf(AccessRule.IncludedDistributionMethods::class.java)
            assertThat((convertedAccessRule as AccessRule.IncludedDistributionMethods).distributionMethods)
                .containsExactlyInAnyOrderElementsOf(listOf(DistributionMethod.STREAM, DistributionMethod.DOWNLOAD))
        }

        @Test
        fun `converts nullable name to empty string`() {
            val accessRule = AccessRuleRequestFactory.sampleIncludedDistributionMethodsAccessRuleRequest(
                name = null,
                distributionMethods = listOf()
            )
            val convertedAccessRule = converter.fromRequest(accessRule)

            assertThat(convertedAccessRule.name).isEqualTo("")
        }

        @Test
        fun `converts excluded languages to access rule`() {
            val accessRule = AccessRuleRequestFactory.sampleExcludedLanguagesAccessRuleRequest(
                name = "access-rule",
                languages = setOf(Locale.ENGLISH.toLanguageTag(), Locale.JAPANESE.toLanguageTag())
            )
            val convertedAccessRule = converter.fromRequest(accessRule)

            assertThat(convertedAccessRule.name).isEqualTo(accessRule.name)
            assertThat(convertedAccessRule).isInstanceOf(AccessRule.ExcludedLanguages::class.java)
            assertThat((convertedAccessRule as AccessRule.ExcludedLanguages).languages)
                .containsExactlyInAnyOrderElementsOf(setOf(Locale.ENGLISH, Locale.JAPANESE))
        }

        @Test
        fun `converts excluded playback sources to access rule`() {
            val accessRule = AccessRuleRequestFactory.sampleExcludedPlaybackSourcesAccessRuleRequest(
                sources = setOf(PlaybackSource.KALTURA.name, PlaybackSource.YOUTUBE.name)
            )
            val convertedAccessRule = converter.fromRequest(accessRule)

            assertThat(convertedAccessRule.name).isEqualTo(accessRule.name)
            assertThat(convertedAccessRule).isInstanceOf(AccessRule.ExcludedPlaybackSources::class.java)
            assertThat((convertedAccessRule as AccessRule.ExcludedPlaybackSources).sources)
                .containsExactlyInAnyOrderElementsOf(setOf(PlaybackSource.KALTURA, PlaybackSource.YOUTUBE))
        }

        @Test
        fun `converts excluded playback sources access rule to resource`() {
            val accessRule = AccessRuleFactory.sampleExcludedPlaybackSourcesAccessRule(
                sources = setOf(PlaybackSource.KALTURA, PlaybackSource.YOUTUBE)
            )
            val resource = converter.toResource(accessRule)

            assertThat(resource.name).isEqualTo(accessRule.name)
            assertThat(resource).isInstanceOf(AccessRuleResource.ExcludedPlaybackSources::class.java)
            assertThat((resource as AccessRuleResource.ExcludedPlaybackSources).sources)
                .containsExactlyInAnyOrderElementsOf(setOf(PlaybackSource.KALTURA.name, PlaybackSource.YOUTUBE.name))
        }

        @Test
        fun `converts included private channels access rule to resource`() {
            val accessRule = AccessRuleFactory.sampleIncludedPrivateChannelsAccessRule(
                channelIds = listOf(ChannelId("hello"), ChannelId("hi"))
            )
            val resource = converter.toResource(accessRule)

            assertThat(resource.name).isEqualTo(accessRule.name)
            assertThat(resource).isInstanceOf(AccessRuleResource.IncludedPrivateChannels::class.java)
            assertThat((resource as AccessRuleResource.IncludedPrivateChannels).channelIds)
                .containsExactlyInAnyOrderElementsOf(listOf("hello","hi"))
        }

        @Test
        fun `throws when invalid playback source is used`() {
            val accessRule = AccessRuleRequestFactory.sampleExcludedPlaybackSourcesAccessRuleRequest(
                sources = setOf("invalid_source")
            )
            val thrown = assertThrows<IllegalArgumentException> { converter.fromRequest(accessRule) }

            assertThat(thrown).isExactlyInstanceOf(IllegalArgumentException::class.java)
        }
    }

    private val converter = AccessRuleConverter()
}
