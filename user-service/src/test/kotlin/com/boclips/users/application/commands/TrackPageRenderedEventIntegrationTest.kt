package com.boclips.users.application.commands

import com.boclips.eventbus.events.page.PageRendered
import com.boclips.users.api.request.PageRenderedEventRequest
import com.boclips.users.api.request.Viewport
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TrackPageRenderedEventIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var trackPageRenderedEvent: TrackPageRenderedEvent

    @Test
    fun `page rendered event is published`() {
        val pageUrl = "http://test.com/test/data?key=value"
        trackPageRenderedEvent.invoke(
            PageRenderedEventRequest(
                url = pageUrl,
                viewport = Viewport(320, 640),
                isResize = true
            )
        )

        val event = eventBus.getEventOfType(PageRendered::class.java)
        assertThat(event.url).isEqualTo(pageUrl)
        assertThat(event.viewport.width).isEqualTo(320)
        assertThat(event.viewport.height).isEqualTo(640)
        assertThat(event.isResize).isTrue
    }

    @Test
    fun `can handle null resize events`() {
        val pageUrl = "http://test.com/test/data?key=value"
        trackPageRenderedEvent.invoke(
            PageRenderedEventRequest(
                url = pageUrl,
                viewport = Viewport(320, 640),
                isResize = null
            )
        )

        val event = eventBus.getEventOfType(PageRendered::class.java)
        val resize: Boolean? = event.isResize

        assertThat(event.url).isEqualTo(pageUrl)
        assertThat(resize).isNull()
    }
}
