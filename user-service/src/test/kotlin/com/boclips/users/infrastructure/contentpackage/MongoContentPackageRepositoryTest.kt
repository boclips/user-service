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

    @Test
    fun `can find all content packages`() {
        val contentPackage1 = ContentPackageFactory.sample(name = "first cp")
        val contentPackage2 = ContentPackageFactory.sample(name = "second cp")
        contentPackageRepository.save(contentPackage1)
        contentPackageRepository.save(contentPackage2)

        val retrievedContentPackages = contentPackageRepository.findAll()

        assertThat(retrievedContentPackages.size).isEqualTo(2)
        assertThat(retrievedContentPackages[0]).isEqualTo(contentPackage1)
        assertThat(retrievedContentPackages[1]).isEqualTo(contentPackage2)
    }
}
