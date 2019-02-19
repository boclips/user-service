package com.boclips.users.domain.service

import com.boclips.users.domain.model.Event
import com.boclips.users.domain.model.EventType
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test

class AccountServiceTest {

    val userRepository = mock<AccountRepository>()
    val analyticsClient = mock<AnalyticsClient>()
    val subject = UserService(
        userRepository,
        analyticsClient
    )

    @Test
    fun `register user when no user creates inactive user`() {
        subject.registerUserIfNew(IdentityId(value = "doesn't exist"))

        verify(userRepository).save(Account(id = "doesn't exist", activated = false))
    }

    @Test
    fun `register user when no user sends activation event`() {
        subject.registerUserIfNew(IdentityId(value = "doesn't exist"))

        verify(analyticsClient).track(
            Event(
                EventType.ACCOUNT_CREATED,
                "doesn't exist"
            )
        )
    }

    @Test
    fun `register user when user exists returns current user`() {
        whenever(userRepository.findById("exists")).thenReturn(Account(id = "exists", activated = true))
        subject.registerUserIfNew(IdentityId(value = "exists"))

        verify(userRepository).findById("exists")
        verifyNoMoreInteractions(userRepository)
    }
}