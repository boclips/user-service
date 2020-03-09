package com.boclips.users.infrastructure.accessrules

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.testsupport.factories.AccessRuleDocumentFactory
import com.boclips.users.testsupport.factories.AccessRuleFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AccessRuleDocumentConverterTest {
    @Nested
    inner class ConvertingSelectedCollections {
        @Test
        fun `converts document to domain object`() {
            val document = AccessRuleDocumentFactory.sampleSelectedCollectionsAccessRuleDocument(
                collectionIds = listOf("A", "B", "C")
            )

            val accessRule = converter.fromDocument(document) as AccessRule.SelectedCollections

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
            val accessRule = AccessRuleFactory.sampleSelectedCollectionsAccessRule()

            val document = converter.toDocument(accessRule) as AccessRuleDocument.SelectedCollections

            assertThat(document.id.toHexString()).isEqualTo(accessRule.id.value)
            assertThat(document.name).isEqualTo(accessRule.name)
            assertThat(document.collectionIds.map { CollectionId(it) }).isEqualTo(accessRule.collectionIds)
        }
    }

    @Nested
    inner class ConvertingSelectedVideos {
        @Test
        fun `converts document to domain object`() {
            val document = AccessRuleDocumentFactory.sampleSelectedVideosAccessRuleDocument(
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
            val accessRule = AccessRuleFactory.sampleSelectedVideosAccessRule()

            val document = converter.toDocument(accessRule) as AccessRuleDocument.IncludedVideos

            assertThat(document.id.toHexString()).isEqualTo(accessRule.id.value)
            assertThat(document.name).isEqualTo(accessRule.name)
            assertThat(document.videoIds.map { VideoId(it) }).isEqualTo(accessRule.videoIds)
        }
    }

    private val converter = AccessRuleDocumentConverter()
}
