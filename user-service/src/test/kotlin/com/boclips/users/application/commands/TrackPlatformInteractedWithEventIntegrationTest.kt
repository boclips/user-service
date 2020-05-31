package com.boclips.users.application.commands

import com.boclips.eventbus.events.platform.PlatformInteractedWith
import com.boclips.security.testing.setSecurityContext
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TrackPlatformInteractedWithEventIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var trackPlatformInteractedWithEvent: TrackPlatformInteractedWithEvent

    @Test
    fun `platform interacted with event is published`() {
        saveUser(UserFactory.sample(id = "testUser"))
        setSecurityContext("testUser")

        trackPlatformInteractedWithEvent.invoke("HELP_CLICKED", "https://teachers.boclips.com", false)

        val event = eventBus.getEventOfType(PlatformInteractedWith::class.java)
        Assertions.assertThat(event.subtype).isEqualTo("HELP_CLICKED")
        Assertions.assertThat(event.userId).isEqualTo("testUser")
        Assertions.assertThat(event.url).isEqualTo("https://teachers.boclips.com")
    }

    @Test
    fun `platform interacted with event is published with no user ID when anon`() {
        saveUser(UserFactory.sample(id = "testUser"))
        setSecurityContext("testUser")

        trackPlatformInteractedWithEvent.invoke("HELP_CLICKED", "https://teachers.boclips.com", true)

        val event = eventBus.getEventOfType(PlatformInteractedWith::class.java)
        Assertions.assertThat(event.subtype).isEqualTo("HELP_CLICKED")
        Assertions.assertThat(event.userId).isEqualTo(null)
        Assertions.assertThat(event.url).isEqualTo("https://teachers.boclips.com")
    }
}
