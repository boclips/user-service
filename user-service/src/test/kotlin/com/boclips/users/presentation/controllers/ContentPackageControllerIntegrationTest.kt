package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asHqUser
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.AccessRuleFactory
import com.boclips.users.testsupport.factories.ContentPackageFactory
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ContentPackageControllerIntegrationTest : AbstractSpringIntegrationTest() {

    @Nested
    inner class GetContentPackage {
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
    }

    @Nested
    inner class UpdateContentPackage {
        @Test
        fun `can update a content package's name and its access rules`() {
            val contentPackage = ContentPackageFactory.sample(
                name = "My first content package",
                accessRules = listOf(
                    AccessRuleFactory.sampleIncludedVideosAccessRule(
                        name = "My access rule for content package",
                        videoIds = listOf(VideoId("123"))
                    )
                )
            )
            saveContentPackage(contentPackage)

            val content =
                """{
                    "name" : "my new title",
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
                .andExpect(
                    jsonPath(
                        "$._embedded.contentPackages[0].accessRules[0].name",
                        Matchers.equalTo("updated access rule")
                    )
                )
                .andExpect(
                    jsonPath(
                        "$._embedded.contentPackages[0].accessRules[0].type",
                        Matchers.equalTo("IncludedVideos")
                    )
                )
                .andExpect(
                    jsonPath(
                        "$._embedded.contentPackages[0].accessRules[0].videoIds",
                        Matchers.containsInAnyOrder("123", "345")
                    )
                )
        }
    }

    @Nested
    inner class CreateContentPackage {

        @Test
        fun `can create a content package`() {
            val accessRuleType = "IncludedVideos"
            val accessRuleName = "video-access-rule"
            val includedVideoId = "video-1"

            mvc.perform(
                post("/v1/content-packages").content(
                    """
                    {
                        "name": "content-package-name",
                        "accessRules": [
                            {
                                "type":"$accessRuleType",
                                "name":"$accessRuleName",
                                "videoIds":["$includedVideoId"]
                            }
                        ]
                    }
                    """.trimIndent()
                ).asHqUser()
            )
                .andExpect(status().isCreated)
                .andDo { result ->
                    val location = result.response.getHeaderValue("location")
                    mvc.perform(
                        MockMvcRequestBuilders.get(location.toString()).asHqUser()
                    )
                        .andExpect(status().isOk)
                        .andExpect(jsonPath("$.name", CoreMatchers.equalTo("content-package-name")))
                        .andExpect(jsonPath("$.accessRules", CoreMatchers.not(Matchers.empty<Any>())))
                        .andExpect(jsonPath("$.accessRules[0].type", CoreMatchers.equalTo(accessRuleType)))
                        .andExpect(jsonPath("$.accessRules[0].name", CoreMatchers.equalTo(accessRuleName)))
                        .andExpect(jsonPath("$.accessRules[0].videoIds", Matchers.hasSize<Any>(1)))
                        .andExpect(jsonPath("$.accessRules[0].videoIds[0]", CoreMatchers.equalTo(includedVideoId)))
                }
        }

        @Test
        fun `cannot create a content package without correct roles`() {
            mvc.perform(
                post("/v1/content-packages").content(
                    """
                    {
                        "name": "content-package-name",
                        "accessRules": []
                    }
                    """.trimIndent()
                ).asUser("hax0rr@gimmeaccess.gov")
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `returns conflict when trying to create a pre-existing content package`() {
            saveContentPackage(ContentPackageFactory.sample(name = "pre-existing content package"))

            mvc.perform(
                post("/v1/content-packages").content(
                    """
                    {
                        "name": "pre-existing content package",
                        "accessRules": []
                    }
                    """.trimIndent()
                ).asHqUser()
            ).andExpect(status().isConflict)
        }
    }
}
