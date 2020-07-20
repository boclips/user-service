package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.presentation.converters.ContentPackageConverter
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccessRuleFactory
import com.boclips.users.testsupport.factories.ContentPackageFactory
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ContentPackageConverterTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var contentPackageConverter: ContentPackageConverter

    @Test
    fun `can convert to a resource`() {
        val packageId = ObjectId.get().toHexString()
        val collectionAccessRule = AccessRuleFactory.sampleIncludedCollectionsAccessRule(
            collectionIds = listOf(CollectionId("hello"), CollectionId("collection")),
            name = "name of the accessRule"
        )

        val contentPackage =
            ContentPackageFactory.sample(
                name = "package",
                id = packageId,
                accessRules = listOf(collectionAccessRule)
            )

        val convertedResource = contentPackageConverter.toContentPackageResource(contentPackage)

        assertThat(convertedResource.id).isEqualTo(packageId)
        assertThat(convertedResource.name).isEqualTo("package")
        assertThat(convertedResource.accessRules).hasSize(1)
        assertThat(convertedResource.accessRules.first().name).isEqualTo("name of the accessRule")
    }

    @Test
    fun `can convert a list of content packages`() {
        val collectionAccessRule = AccessRuleFactory.sampleIncludedCollectionsAccessRule(
            collectionIds = listOf(CollectionId("hello"), CollectionId("collection")),
            name = "name of the accessRule"
        )

        val contentPackage1 =
            ContentPackageFactory.sample(
                name = "package one",
                accessRules = listOf(collectionAccessRule)
            )

        val contentPackage2 =
            ContentPackageFactory.sample(
                name = "package two",
                accessRules = listOf(collectionAccessRule)
            )

        val convertedResource = contentPackageConverter.toContentPackagesResource(listOf(contentPackage1, contentPackage2))

        assertThat(convertedResource._embedded.contentPackages).isNotNull
        assertThat(convertedResource._embedded.contentPackages).hasSize(2)
        assertThat(convertedResource._embedded.contentPackages[0].id).isEqualTo(contentPackage1.id.value)
        assertThat(convertedResource._embedded.contentPackages[1].id).isEqualTo(contentPackage2.id.value)
    }
}

