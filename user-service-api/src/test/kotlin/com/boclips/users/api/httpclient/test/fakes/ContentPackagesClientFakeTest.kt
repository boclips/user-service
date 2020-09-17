package com.boclips.users.api.httpclient.test.fakes

import com.boclips.users.api.factories.AccessRuleResourceFactory
import com.boclips.users.api.response.accessrule.ContentPackageResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ContentPackagesClientFakeTest {
    @Test
    fun `can fetch a content package`() {
        val fake = ContentPackagesClientFake()
        fake.add(
            ContentPackageResource(
                id = "my-id",
                name = "name",
                accessRules = listOf(
                    AccessRuleResourceFactory.sampleIncludedCollections(),
                    AccessRuleResourceFactory.sampleIncludedCollections()
                ),
                _links = mapOf()
            )
        )

        val resource = fake.find("my-id")

        assertThat(resource.id).isEqualTo("my-id")
        assertThat(resource.name).isEqualTo("name")
        assertThat(resource.accessRules).hasSize(2)
    }
}
