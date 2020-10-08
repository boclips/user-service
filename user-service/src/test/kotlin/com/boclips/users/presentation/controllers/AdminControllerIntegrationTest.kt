package com.boclips.users.presentation.controllers

import com.boclips.eventbus.events.contentpackage.ContentPackageBroadcastRequested
import com.boclips.eventbus.events.user.UserBroadcastRequested
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asOperator
import com.boclips.users.testsupport.asTeacher
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminControllerIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `trigger broadcasting all users`() {
        saveUser(UserFactory.sample())

        mockMvc.perform(post("/v1/admin/users/actions/broadcast_users").asOperator())
            .andExpect(status().isOk)

        assertThat(eventBus.countEventsOfType(UserBroadcastRequested::class.java)).isEqualTo(1)
    }

    @Test
    fun `trigger broadcasting all users is restricted by roles`() {
        saveUser(UserFactory.sample())

        mockMvc.perform(post("/v1/admin/users/actions/broadcast_users").asTeacher())
            .andExpect(status().isForbidden)

        assertThat(eventBus.countEventsOfType(UserBroadcastRequested::class.java)).isEqualTo(0)
    }

    @Test
    fun `trigger broadcasting all content packages`() {
        saveContentPackage(ContentPackageFactory.sample())
        saveContentPackage(ContentPackageFactory.sample())

        mockMvc.perform(post("/v1/admin/users/actions/broadcast_content_packages").asOperator())
            .andExpect(status().isOk)

        assertThat(eventBus.countEventsOfType(ContentPackageBroadcastRequested::class.java)).isEqualTo(2)
    }

    @Test
    fun `trigger broadcasting all content packages users is restricted by roles`() {
        saveContentPackage(ContentPackageFactory.sample())
        saveContentPackage(ContentPackageFactory.sample())

        mockMvc.perform(post("/v1/admin/users/actions/broadcast_content_packages").asTeacher())
            .andExpect(status().isForbidden)

        assertThat(eventBus.countEventsOfType(ContentPackageBroadcastRequested::class.java)).isEqualTo(0)
    }
}
