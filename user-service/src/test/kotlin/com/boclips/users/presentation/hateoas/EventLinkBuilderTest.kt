package com.boclips.users.presentation.hateoas

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder

class EventLinkBuilderTest : AbstractSpringIntegrationTest() {

    private lateinit var eventLinkBuilder: EventLinkBuilder

    @AfterEach
    fun cleanUp() {
        SecurityContextHolder.clearContext()
    }

    @BeforeEach
    fun setUp() {
        eventLinkBuilder = EventLinkBuilder()
    }

    @Test
    fun `trackPageRendered link provided`() {
        val trackPageRendered = eventLinkBuilder.logPageRenderedEventLink()

        assertThat(trackPageRendered).isNotNull
        assertThat(trackPageRendered!!.href).endsWith("/events/page-render")
        assertThat(trackPageRendered.rel.value()).endsWith("trackPageRendered")
    }

    @Test
    fun `trackPlatformInteractedWith link provided`() {
        val trackPlatformInteractedAnonymously = eventLinkBuilder.trackPlatformInteractedWithEventLink()

        assertThat(trackPlatformInteractedAnonymously).isNotNull
        assertThat(trackPlatformInteractedAnonymously!!.href).endsWith("/events/platform-interaction{?subtype,anonymous}")
        assertThat(trackPlatformInteractedAnonymously.rel.value()).endsWith("trackPlatformInteractedWith")
    }
}
