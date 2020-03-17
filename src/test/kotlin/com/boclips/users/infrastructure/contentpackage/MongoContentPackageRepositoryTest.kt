package com.boclips.users.infrastructure.contentpackage

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.ContentPackageFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoContentPackageRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `can find a content package by id`() {
        val contentPackage = ContentPackageFactory.sample()
        contentPackageRepository.save(contentPackage)

        val retrievedContentPackage = contentPackageRepository.findById(contentPackage.id)

        assertThat(contentPackage).isEqualTo(retrievedContentPackage)
    }

    @Test
    fun `can find a content package by name`() {
        val contentPackage = ContentPackageFactory.sample(name = "a cp")
        contentPackageRepository.save(contentPackage)

        val retrievedContentPackage = contentPackageRepository.findByName("a cp")

        assertThat(contentPackage).isEqualTo(retrievedContentPackage)
    }
}
