package com.boclips.users.presentation.hateoas

import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ContentPackageLinkBuilderTest : AbstractSpringIntegrationTest() {
    @Test
    fun `creates a self link to content package`() {
        val contentPackageId = "test-package-id"

        val selfLink = contentPackageLinkBuilder.self(ContentPackageId(contentPackageId))

        Assertions.assertThat(selfLink.rel.value()).isEqualTo("self")
        Assertions.assertThat(selfLink.href).endsWith("/v1/content-packages/$contentPackageId")
    }
}
