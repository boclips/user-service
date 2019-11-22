package com.boclips.users.domain.service.events

import com.boclips.eventbus.events.page.PageRendered
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired

class EventServiceTest: AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var eventService: EventService

    @Test
    fun `publish page rendered events`() {
        eventService.publishPageRendered(userId = "test-id", url = "https://teachers.boclips.com/collections")

        val event = eventBus.getEventOfType(PageRendered::class.java)
        assertThat(event.url).isEqualTo("https://teachers.boclips.com/collections")
        assertThat(event.userId).isEqualTo("test-id")
    }
}
