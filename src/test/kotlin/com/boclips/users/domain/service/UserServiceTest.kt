package com.boclips.users.domain.service

import com.boclips.users.domain.model.events.AnalyticsClient
import com.boclips.users.domain.model.events.Event
import com.boclips.users.domain.model.events.EventType
import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.testsupport.UserFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.mockito.internal.verification.Times

class UserServiceTest {

    val analyticsClient = mock<AnalyticsClient>()
    val identityProvider = mock<IdentityProvider>()
    val subject = UserService(
        identityProvider = identityProvider,
        analyticsClient = analyticsClient
    )

    @Test
    fun `fires event to mixpanel if user is new`() {
        val user = UserFactory.sample(activated = false)
        whenever(identityProvider.getUserById(any())).thenReturn(user)

        subject.registerUserIfNew(user.keycloakId.value)

        verify(analyticsClient).track(Event(EventType.ACCOUNT_CREATED, user.keycloakId.value))
    }

    @Test
    fun `register user when user exists returns current user`() {
        val user = UserFactory.sample(activated = true)
        whenever(identityProvider.getUserById(any())).thenReturn(user)

        subject.registerUserIfNew(user.keycloakId.value)

        verify(analyticsClient, Times(0)).track(any())
    }
}