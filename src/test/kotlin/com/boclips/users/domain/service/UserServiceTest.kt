package com.boclips.users.domain.service

import com.boclips.users.domain.model.users.User
import com.boclips.users.domain.model.users.UserRepository
import com.boclips.users.domain.model.events.AnalyticsClient
import com.boclips.users.domain.model.events.Event
import com.boclips.users.domain.model.events.EventType
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test

class UserServiceTest {

    val userRepository = mock<UserRepository>()
    val analyticsClient = mock<AnalyticsClient>()
    val subject = UserService(
            userRepository,
            analyticsClient
    )

    @Test
     fun `register user when no user creates inactive user`() {
        subject.registerUserIfNew("doesn't exist")

        verify(userRepository).save(User(id = "doesn't exist", activated = false))
    }

    @Test
     fun `register user when no user sends activation event`() {
        subject.registerUserIfNew("doesn't exist")

        verify(analyticsClient).track(Event(EventType.BEGIN_ACTIVATION, "doesn't exist"))
    }

    @Test
     fun `register user when user exists returns current user`() {
        whenever(userRepository.findById("exists")).thenReturn(User(id = "exists", activated = true))
        subject.registerUserIfNew("exists")

        verify(userRepository).findById("exists")
        verifyNoMoreInteractions(userRepository)
    }
}