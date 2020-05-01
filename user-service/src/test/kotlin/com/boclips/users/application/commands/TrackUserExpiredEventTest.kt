package com.boclips.users.application.commands

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.service.events.EventService
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.check
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime

class TrackUserExpiredEventTest : AbstractSpringIntegrationTest() {

    lateinit var trackUserExpiredEvent: TrackUserExpiredEvent
    lateinit var mockEventService: EventService

    @BeforeEach
    fun setup() {
        mockEventService = mock()

        trackUserExpiredEvent = TrackUserExpiredEvent(
            getOrImportUser = getOrImportUser,
            eventService = mockEventService,
            accessExpiryService = accessExpiryService
        )
    }

    @Test
    fun `it emits a UserExpired event if the user has expired`() {
        val user = saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    id = "user-id"
                ),
                accessExpiresOn = ZonedDateTime.now(ZoneOffset.UTC).minusDays(1)
            )
        )

        setSecurityContext(user.id.value)

        trackUserExpiredEvent()

        verify(mockEventService).publishUserExpired(check<User> {
            assertThat(it).isEqualTo(user)
        })
    }

    @Test
    fun `it does not emit a UserExpired event if the user has not expired`() {
        val user = saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    id = "user-id"
                ),
                accessExpiresOn = ZonedDateTime.now(ZoneOffset.UTC).plusDays(1)
            )
        )

        setSecurityContext(user.id.value)

        trackUserExpiredEvent()

        verify(mockEventService, never()).publishUserExpired(any())
    }

    @Test
    fun `it does not emit a UserExpired event if the user is anonymous`() {
        trackUserExpiredEvent()

        verify(mockEventService, never()).publishUserExpired(any())
    }
}
