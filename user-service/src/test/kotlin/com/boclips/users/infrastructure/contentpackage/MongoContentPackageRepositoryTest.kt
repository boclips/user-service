package com.boclips.users.infrastructure.contentpackage

import com.boclips.users.application.exceptions.ContentPackageNotFoundException
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccessRuleFactory
import com.boclips.users.testsupport.factories.ContentPackageFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

    @Test
    fun `can save a content package with access rules`() {
        val firstAccessRuleName = "firstAccessRuleName"
        val secondAccessRuleName = "secondAccessRuleName"
        val firstAccessRule = AccessRuleFactory.sampleExcludedVideosAccessRule(
            name = firstAccessRuleName,
            videoIds = listOf(VideoId("video-1"))
        )
        val secondAccessRule = AccessRuleFactory.sampleIncludedCollectionsAccessRule(
            name = secondAccessRuleName,
            collectionIds = listOf(CollectionId("collection-1"))
        )
        val contentPackage = ContentPackageFactory.sample(accessRules = listOf(firstAccessRule, secondAccessRule))
        contentPackageRepository.save(contentPackage)

        val retrievedContentPackage = contentPackageRepository.findById(contentPackage.id)

        assertThat(contentPackage).isEqualTo(retrievedContentPackage)
        assertThat(contentPackage.accessRules).hasSize(2)
        assertThat(contentPackage.accessRules.first().name).isEqualTo(firstAccessRuleName)
        assertThat(contentPackage.accessRules[1].name).isEqualTo(secondAccessRuleName)
    }

    @Test
    fun `can update a content package`() {
        val firstAccessRule = AccessRuleFactory.sampleExcludedVideosAccessRule(
            videoIds = listOf(VideoId("video-1"))
        )

        val contentPackage = ContentPackageFactory.sample(accessRules = listOf(firstAccessRule))
        contentPackageRepository.save(contentPackage)

        val newAccessRule = AccessRuleFactory.sampleIncludedCollectionsAccessRule(
            name = "secondAccessRuleName",
            collectionIds = listOf(CollectionId("collection-1"))
        )

        val updatedContentPackage = ContentPackageFactory.sample(
            id = contentPackage.id.value,
            name = "updated name",
            accessRules = listOf(newAccessRule)
        )

        val afterUpdateContentPackage = contentPackageRepository.replaceContentPackage(updatedContentPackage)

        assertThat(afterUpdateContentPackage).isEqualTo(updatedContentPackage)
    }
}
