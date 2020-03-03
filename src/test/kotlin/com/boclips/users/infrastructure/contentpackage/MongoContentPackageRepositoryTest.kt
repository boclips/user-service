package com.boclips.users.infrastructure.contentpackage

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.ContentPackageFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoContentPackageRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `can find a content package by id`() {
        val contentPackage = ContentPackageFactory.sampleContentPackage()
        contentPackageRepository.save(contentPackage)
        val retrievedContentPackage = contentPackageRepository.findById(contentPackage.id)
        assertThat(contentPackage).isEqualTo(retrievedContentPackage)
    }
}
