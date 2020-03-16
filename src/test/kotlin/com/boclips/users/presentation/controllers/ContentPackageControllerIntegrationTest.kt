package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.ContentPackageFactory
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ContentPackageControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `can fetch a content package by id`() {
        val contentPackage = ContentPackageFactory.sample(name = "content-package")
        saveContentPackage(contentPackage)

        mvc.perform(
                MockMvcRequestBuilders.get("/v1/content-packages/${contentPackage.id.value}")
                    .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_CONTENT_PACKAGES)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name", Matchers.equalTo("content-package")))
            .andExpect(
                jsonPath(
                    "$._links.self.href",
                    Matchers.endsWith("/v1/content-packages/${contentPackage.id.value}")
                )
            )
    }

    @Test
    fun `gets 403 without correct role`() {
        mvc.perform(
            MockMvcRequestBuilders.get("/v1/content-packages/id").asUserWithRoles("package@madh4xor.com")
        ).andExpect(status().isForbidden)
    }
}
