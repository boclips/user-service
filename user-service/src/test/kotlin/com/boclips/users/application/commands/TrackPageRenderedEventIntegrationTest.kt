package com.boclips.users.application.commands

import com.boclips.eventbus.events.page.PageRendered
import com.boclips.users.presentation.requests.PageRenderedEventRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired

class TrackPageRenderedEventIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var trackPageRenderedEvent: TrackPageRenderedEvent

    @Test
    fun `page rendered event is published`() {
        val pageUrl = "http://test.com/test/data?key=value"
        trackPageRenderedEvent.invoke(PageRenderedEventRequest(pageUrl))

        val event = eventBus.getEventOfType(PageRendered::class.java)
        Assertions.assertThat(event.url).isEqualTo(pageUrl)
    }
}
