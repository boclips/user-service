package com.boclips.users.presentation.controllers

import com.boclips.eventbus.events.page.PageRendered
import com.boclips.security.testing.setSecurityContext
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class EventControllerTest : AbstractSpringIntegrationTest(){

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `page rendered events by trackable user navigation are being saved`() {
        val userId = "test-user-id"
        setSecurityContext(userId)

        val organisationAccount = saveSchool()
        saveUser(UserFactory.sample(account = AccountFactory.sample(id = userId), organisationAccountId = organisationAccount.id))

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

        saveUser(UserFactory.sample(
            account = AccountFactory.sample(id = userId),
            profile = null
        ))

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
}
