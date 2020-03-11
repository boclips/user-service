package com.boclips.users.infrastructure.accessrules

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.domain.model.contentpackage.VideoType
import com.boclips.users.testsupport.factories.AccessRuleDocumentFactory
import com.boclips.users.testsupport.factories.AccessRuleFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AccessRuleDocumentConverterTest {
    @Nested
    inner class ConvertingIncludedCollections {
        @Test
        fun `converts document to domain object`() {
            val document = AccessRuleDocumentFactory.sampleIncludedCollectionsAccessRuleDocument(
                collectionIds = listOf("A", "B", "C")
            )

            val accessRule = converter.fromDocument(document) as AccessRule.IncludedCollections

            assertThat(accessRule.id.value).isEqualTo(document.id.toHexString())
            assertThat(accessRule.name).isEqualTo(document.name)
            assertThat(accessRule.collectionIds).containsExactlyInAnyOrder(
                CollectionId("A"),
                CollectionId("B"),
                CollectionId("C")
            )
        }

        @Test
        fun `converts domain object to document`() {
            val accessRule = AccessRuleFactory.sampleIncludedCollectionsAccessRule()

            val document = converter.toDocument(accessRule) as AccessRuleDocument.IncludedCollections

            assertThat(document.id.toHexString()).isEqualTo(accessRule.id.value)
            assertThat(document.name).isEqualTo(accessRule.name)
            assertThat(document.collectionIds.map { CollectionId(it) }).isEqualTo(accessRule.collectionIds)
        }
    }

    @Nested
    inner class ConvertingIncludedVideos {
        @Test
        fun `converts document to domain object`() {
            val document = AccessRuleDocumentFactory.sampleIncludedVideosAccessRuleDocument(
                videoIds = listOf("A", "B", "C")
            )

            val accessRule = converter.fromDocument(document) as AccessRule.IncludedVideos

            assertThat(accessRule.id.value).isEqualTo(document.id.toHexString())
            assertThat(accessRule.name).isEqualTo(document.name)
            assertThat(accessRule.videoIds).containsExactlyInAnyOrder(
                VideoId("A"),
                VideoId("B"),
                VideoId("C")
            )
        }

        @Test
        fun `converts domain object to document`() {
            val accessRule = AccessRuleFactory.sampleIncludedVideosAccessRule()

            val document = converter.toDocument(accessRule) as AccessRuleDocument.IncludedVideos

            assertThat(document.id.toHexString()).isEqualTo(accessRule.id.value)
            assertThat(document.name).isEqualTo(accessRule.name)
            assertThat(document.videoIds.map { VideoId(it) }).isEqualTo(accessRule.videoIds)
        }
    }

    @Nested
    inner class ConvertingExcludedVideos {
        @Test
        fun `symmetrical conversion to domain`() {
            val accessRule = AccessRuleFactory.sampleExcludedVideosAccessRule()

            val document = converter.toDocument(accessRule)
            val convertedRule = converter.fromDocument(document)

            assertThat(accessRule).isEqualTo(convertedRule)
        }
    }

    @Nested
    inner class ConvertingExcludedVideoTypes {
        @Test
        fun `symmetrical conversion to domain`() {
            val accessRule = AccessRuleFactory.sampleExcludedVideoTypesAccessRule(videoTypes = listOf(VideoType.NEWS))

            val document = converter.toDocument(accessRule)
            val convertedRule = converter.fromDocument(document)

            assertThat(accessRule).isEqualTo(convertedRule)
        }
    }

    private val converter = AccessRuleDocumentConverter()
}
