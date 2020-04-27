package com.boclips.users.presentation.controllers

import com.boclips.eventbus.events.page.PageRendered
import com.boclips.eventbus.events.platform.PlatformInteractedWith
import com.boclips.eventbus.events.user.UserExpired
import com.boclips.security.testing.setSecurityContext
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.school
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.ZoneOffset
import java.time.ZonedDateTime

class EventControllerTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `page rendered events by trackable user navigation are being saved`() {
        val userId = "test-user-id"
        setSecurityContext(userId)

        val organisationAccount = saveOrganisation(school())
        saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = userId),
                organisation = organisationAccount
            )
        )

        val path = "/v1/events/page-render"
        val content = """{
                    "url" : "http://teachers.boclips.com/discover-collections?subject=5cb499c9fd5beb4281894553"
                    }""".trimMargin()

        mockMvc.perform(
            MockMvcRequestBuilders.post(path)
                .asUser(id = userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(MockMvcResultMatchers.status().isCreated)

        val event = eventBus.getEventOfType(PageRendered::class.java)

        assertThat(event.userId).isEqualTo("test-user-id")
        assertThat(event.url).isEqualTo("http://teachers.boclips.com/discover-collections?subject=5cb499c9fd5beb4281894553")
    }

    @Test
    fun `page rendered events by untrackable user does not contain UserId`() {
        val userId = "test-user-id"
        setSecurityContext(userId)

        saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = userId),
                profile = null
            )
        )

        val path = "/v1/events/page-render"
        val content = """{
                    "url" : "http://teachers.boclips.com/discover-collections?subject=5cb499c9fd5beb4281894553"
                    }""".trimMargin()

        mockMvc.perform(
            MockMvcRequestBuilders.post(path)
                .asUser(id = userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(MockMvcResultMatchers.status().isCreated)

        val event = eventBus.getEventOfType(PageRendered::class.java)

        assertThat(event.userId).isEqualTo("anonymousUser")
        assertThat(event.url).isEqualTo("http://teachers.boclips.com/discover-collections?subject=5cb499c9fd5beb4281894553")
    }

    @Test
    fun `it emits a user expired event when requested`() {
        val userId = "expired-user-id"
        setSecurityContext(userId)

        saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = userId),
                accessExpiresOn = ZonedDateTime.now(ZoneOffset.UTC).minusDays(1)
            )
        )

        val path = "/v1/events/expired-user-access"

        mockMvc.perform(
            MockMvcRequestBuilders.post(path)
                .asUser(id = userId)
        ).andExpect(MockMvcResultMatchers.status().isCreated)

        val event = eventBus.getEventOfType(UserExpired::class.java)

        assertThat(event.user.id).isEqualTo("expired-user-id")
    }


    @Test
    fun `platform interaction is tracked and sets url from referer`() {
        saveUser(UserFactory.sample(id = "testUser"))

        mvc.perform(
            MockMvcRequestBuilders.post("/v1/events/platform-interaction?subtype=HELP_CLICKED").asUser("testUser")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Referer", "https://teachers.boclips.com")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)

        val event = eventBus.getEventOfType(PlatformInteractedWith::class.java)

        assertThat(event.url).isEqualTo("https://teachers.boclips.com")
        assertThat(event.subtype).isEqualTo("HELP_CLICKED")
        assertThat(event.userId).isEqualTo("testUser")
    }
}
