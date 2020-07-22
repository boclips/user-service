package com.boclips.users.presentation.hateoas

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
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
    @Test
    fun `creates a get all content packages link`() {
        setSecurityContext("package-view", UserRoles.VIEW_CONTENT_PACKAGES)

        val getAllLink = contentPackageLinkBuilder.getContentPackagesLink()

        Assertions.assertThat(getAllLink?.rel?.value()).isEqualTo("contentPackages")
        Assertions.assertThat(getAllLink?.href).endsWith("/v1/content-packages")
    }

    @Test
    fun `creates a get content package link`() {
        setSecurityContext("package-view", UserRoles.VIEW_CONTENT_PACKAGES)

        val getLink = contentPackageLinkBuilder.getContentPackageLink()

        Assertions.assertThat(getLink?.rel?.value()).isEqualTo("contentPackage")
        Assertions.assertThat(getLink?.href).endsWith("/v1/content-packages/{id}")
    }
}
