package com.boclips.users.infrastructure.contentpackage

import com.boclips.users.testsupport.factories.ContentPackageFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ContentPackageDocumentConverterTest {
    @Test
    fun `document conversion is symmetrical`() {
        val contentPackage = ContentPackageFactory.sampleContentPackage();
        val convertedDocument = ContentPackageDocumentConverter.toDocument(contentPackage)
        val convertedPackage = ContentPackageDocumentConverter.fromDocument(convertedDocument)

        assertThat(contentPackage).isEqualTo(convertedPackage)
    }
}
