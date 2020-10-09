package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.ContentPackageFactory
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ContentPackageTestSupportControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `can create a content package`() {
        val accessRuleType = "IncludedVideos"
        val accessRuleName = "video-access-rule"
        val includedVideoId = "video-1"

        val userId = "operator@boclips.com"
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
            ).asUserWithRoles(
                userId,
                UserRoles.INSERT_CONTENT_PACKAGES
            )
        )
            .andExpect(status().isCreated)
            .andDo { result ->
                val location = result.response.getHeaderValue("location")
                mvc.perform(
                    get(location.toString()).asUserWithRoles(
                        userId,
                        UserRoles.VIEW_CONTENT_PACKAGES
                    )
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.name", equalTo("content-package-name")))
                    .andExpect(jsonPath("$.accessRules", not(empty<Any>())))
                    .andExpect(jsonPath("$.accessRules[0].type", equalTo(accessRuleType)))
                    .andExpect(jsonPath("$.accessRules[0].name", equalTo(accessRuleName)))
                    .andExpect(jsonPath("$.accessRules[0].videoIds", hasSize<Any>(1)))
                    .andExpect(jsonPath("$.accessRules[0].videoIds[0]", equalTo(includedVideoId)))
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

        val userId = "operator@boclips.com"
        mvc.perform(
            post("/v1/content-packages").content(
                """
                    {
                        "name": "pre-existing content package",
                        "accessRules": []
                    }
                    """.trimIndent()
            ).asUserWithRoles(
                userId,
                UserRoles.INSERT_CONTENT_PACKAGES
            )
        ).andExpect(status().isConflict)
    }
}
