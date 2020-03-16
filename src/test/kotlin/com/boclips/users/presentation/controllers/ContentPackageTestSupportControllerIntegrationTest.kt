package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ContentPackageTestSupportControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `can create a content package`() {
        val accessRule = saveIncludedVideosAccessRule(name = "video-access-rule", videoIds = listOf(VideoId("video-1")))

        val userId = "operator@boclips.com"
        mvc.perform(
                post("/v1/content-packages").content(
                    """
                    {
                        "name": "content-package-name",
                        "accessRuleIds": ["${accessRule.id.value}"]
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
            }
    }

    @Test
    fun `cannot create a content package without correct roles`() {
        val accessRule = saveIncludedVideosAccessRule(name = "video-access-rule", videoIds = listOf(VideoId("video-1")))
        mvc.perform(
            post("/v1/content-packages").content(
                """
                    {
                        "name": "content-package-name",
                        "accessRuleIds": ["${accessRule.id.value}"]
                    }
                    """.trimIndent()
            ).asUser("hax0rr@gimmeaccess.gov")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `returns a 400 response when non-existent access rule is specified`() {
        mvc.perform(
                post("/v1/content-packages").content(
                    """
                    {
                        "name": "content-package-name",
                        "accessRuleIds": ["yolo"]
                    }
                    """.trimIndent()
                ).asUserWithRoles(
                    "test-user@boclips.com",
                    UserRoles.INSERT_CONTENT_PACKAGES
                )
            )
            .andExpect(status().isBadRequest)
            .andExpectApiErrorPayload()
    }
}
