package com.boclips.users.application.commands

import com.boclips.users.api.request.UpdateContentPackageRequest
import com.boclips.users.application.exceptions.ContentPackageNotFoundException
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccessRuleFactory
import com.boclips.users.testsupport.factories.AccessRuleRequestFactory.Companion.sampleIncludedVideosAccessRuleRequest
import com.boclips.users.testsupport.factories.ContentPackageFactory
import org.assertj.core.api.Assertions
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class UpdateContentPackageIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var updateContentPackage: UpdateContentPackage

    @Test
    fun `successfully updates a content package`() {
        val existingContentPackage = saveContentPackage(
            ContentPackageFactory.sample(
                name = "hello",
                accessRules = listOf(
                    AccessRuleFactory.sampleIncludedVideosAccessRule(
                        name = "access rule name",
                        videoIds = listOf(VideoId("123"))
                    )
                )
            )
        )

        val updatedContentPackage = UpdateContentPackageRequest(
            name = "hello",
            accessRules = setOf(
                sampleIncludedVideosAccessRuleRequest(
                    name = "access rule name",
                    videoIds = listOf("123", "456")
                )
            )
        )

        val afterUpdateContentPackage = updateContentPackage(existingContentPackage.id.value, updatedContentPackage)

        Assertions.assertThat(afterUpdateContentPackage.name).isEqualTo("hello")
        Assertions.assertThat(afterUpdateContentPackage.accessRules[0].name).isEqualTo("access rule name")
    }

    @Test
    fun `should fail if trying to replace non existing content package`() {
        assertThrows<ContentPackageNotFoundException> {
            updateContentPackage(
                id = ObjectId().toHexString(),
                updateContentPackageRequest = UpdateContentPackageRequest(
                    name = "hello",
                    accessRules = setOf(
                        sampleIncludedVideosAccessRuleRequest(
                            name = "access rule name",
                            videoIds = listOf("123", "456")
                        )
                    )
                )
            )
        }
    }
}
