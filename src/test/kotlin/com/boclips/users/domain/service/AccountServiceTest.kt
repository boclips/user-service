package com.boclips.users.domain.service

import com.boclips.users.domain.model.AccountMetadata
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.domain.model.analytics.Event
import com.boclips.users.domain.model.analytics.EventType
import com.boclips.users.domain.model.analytics.MixpanelId
import com.boclips.users.domain.model.identity.IdentityId
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountServiceTest {

    val userRepository = mock<AccountRepository>()
    val analyticsClient = mock<AnalyticsClient>()
    val metadataProvider = mock<MetadataProvider>()
    val identityProvider = mock<IdentityProvider>()

    val userService = UserService(
        accountRepository = userRepository,
        identityProvider = identityProvider,
        analyticsClient = analyticsClient,
        metadataProvider = metadataProvider
    )

    @BeforeEach
    fun setUp() {
        whenever(metadataProvider.getMetadata(IdentityId(value = "doesn't exist")))
            .thenReturn(
                AccountMetadata(subjects = "subjects", mixpanelId = MixpanelId(value = "123"))
            )
    }

    @Test
    fun `register user when no user creates inactive user`() {
        userService.registerUserIfNew(IdentityId(value = "doesn't exist"))

        verify(userRepository).save(
            Account(
                id = AccountId(value = "doesn't exist"),
                activated = false,
                analyticsId = MixpanelId(value = "123"),
                subjects = "subjects"
            )
        )
    }

    @Test
    fun `register user when no user sends activation event`() {
        userService.registerUserIfNew(IdentityId(value = "doesn't exist"))

        verify(analyticsClient).track(
            Event(
                EventType.ACCOUNT_CREATED,
                "doesn't exist"
            )
        )
    }

    @Test
    fun `register user when user exists returns current user`() {
        whenever(userRepository.findById(AccountId(value = "exists"))).thenReturn(
            Account(
                id = AccountId(value = "exists"),
                activated = true,
                subjects = "subjects",
                analyticsId = MixpanelId(value = "123")
            )
        )
        userService.registerUserIfNew(IdentityId(value = "exists"))

        verify(userRepository).findById(AccountId(value = "exists"))
        verifyNoMoreInteractions(userRepository)
    }
}