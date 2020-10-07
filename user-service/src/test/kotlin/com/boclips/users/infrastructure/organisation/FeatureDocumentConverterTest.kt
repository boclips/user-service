package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.feature.Feature
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FeatureDocumentConverterTest {
    @Test
    fun `converts to FeatureDocument`() {
        Feature.values().forEach { feature ->
            run {
                val converted = FeatureDocumentConverter.toDocument(feature)
                assertThat(feature.name).isEqualTo(converted.name)
            }
        }
    }

    @Test
    fun `converts to Feature`() {
        FeatureDocument.values().forEach { document ->
            run {
                val converted = FeatureDocumentConverter.fromDocument(document)
                assertThat(document.name).isEqualTo(converted.name)
            }
        }
    }
}