package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.AccessRuleFactory
import com.boclips.users.testsupport.factories.ContentPackageFactory
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ContentPackageControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `gets 403 without correct role`() {
        mvc.perform(
            MockMvcRequestBuilders.get("/v1/content-packages/id").asUserWithRoles("package@madh4xor.com")
        ).andExpect(status().isForbidden)
    }

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
    fun `can get all content packages`() {
        val contentPackageOne = ContentPackageFactory.sample(name = "My first content package")
        val contentPackageTwo = ContentPackageFactory.sample(name = "My second content package")
        saveContentPackage(contentPackageOne)
        saveContentPackage(contentPackageTwo)

        mvc.perform(
            MockMvcRequestBuilders.get("/v1/content-packages")
                .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_CONTENT_PACKAGES)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._embedded.contentPackages.length()", Matchers.equalTo(2)))
            .andExpect(jsonPath("$._embedded.contentPackages[0].id", Matchers.equalTo(contentPackageOne.id.value)))
            .andExpect(jsonPath("$._embedded.contentPackages[0].name", Matchers.equalTo("My first content package")))
            .andExpect(jsonPath("$._embedded.contentPackages[1].id", Matchers.equalTo(contentPackageTwo.id.value)))
            .andExpect(jsonPath("$._embedded.contentPackages[1].name", Matchers.equalTo("My second content package")))
    }

    @Test
    fun `can update a content package's name and its access rules`() {
        val contentPackage = ContentPackageFactory.sample(
            name = "My first content package",
            accessRules = listOf(AccessRuleFactory.sampleIncludedVideosAccessRule(
                id = AccessRuleId("access-rule-1"),
                name = "My access rule for content package",
                videoIds = listOf(VideoId("123"))
            ))
        )
        saveContentPackage(contentPackage)

        val content =
            """{
                    "title" : "my new title",
                    "accessRules": [
                        {
                            "type": "IncludedVideos",
                            "name": "updated access rule",
                            "videoIds": ["123", "345"]
                        }
                    ]

                }""".trimMargin()
        mvc.perform(
            MockMvcRequestBuilders.put("/v1/content-packages/${contentPackage.id.value}")
                .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.UPDATE_CONTENT_PACKAGES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )
            .andExpect(status().isOk)

        mvc.perform(
            MockMvcRequestBuilders.get("/v1/content-packages")
                .asUserWithRoles("contracts-viewer@hacker.com", UserRoles.VIEW_CONTENT_PACKAGES)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._embedded.contentPackages.length()", Matchers.equalTo(1)))
            .andExpect(jsonPath("$._embedded.contentPackages[0].id", Matchers.equalTo(contentPackage.id.value)))
            .andExpect(jsonPath("$._embedded.contentPackages[0].name", Matchers.equalTo("my new title")))
            .andExpect(jsonPath("$._embedded.contentPackages[0].accessRules[0].name", Matchers.equalTo("updated access rule")))
            .andExpect(jsonPath("$._embedded.contentPackages[0].accessRules[0].type", Matchers.equalTo("IncludedVideos")))
            .andExpect(jsonPath("$._embedded.contentPackages[0].accessRules[0].videoIds", Matchers.containsInAnyOrder("123", "345")))
    }
}
