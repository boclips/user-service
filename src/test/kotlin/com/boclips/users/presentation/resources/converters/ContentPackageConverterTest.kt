package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccessRuleFactory
import com.boclips.users.testsupport.factories.ContentPackageFactory
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ContentPackageConverterTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var contentPackageConvert: ContentPackageConverter

    @Test
    fun `can convert to a resource`() {
        val packageId = ObjectId.get().toHexString()
        val collectionAccessRule = AccessRuleFactory.sampleSelectedCollectionsAccessRule(
            collectionIds = listOf(CollectionId("hello"), CollectionId("collection")),
            name = "name of the accessRule"
        )

        val contentPackage =
            ContentPackageFactory.sampleContentPackage(
                name = "package",
                id = packageId,
                accessRules = listOf(collectionAccessRule)
            )

        val convertedResource = contentPackageConvert.toResource(contentPackage)

        assertThat(convertedResource.id).isEqualTo(packageId)
        assertThat(convertedResource.name).isEqualTo("package")
        assertThat(convertedResource.accessRules).hasSize(1)
        assertThat(convertedResource.accessRules.first().name).isEqualTo("name of the accessRule")
    }
}

