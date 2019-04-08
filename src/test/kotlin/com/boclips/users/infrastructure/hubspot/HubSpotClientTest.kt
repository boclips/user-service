package com.boclips.users.infrastructure.hubspot

import com.boclips.users.testsupport.AccountFactory
import com.boclips.users.testsupport.UserFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.sun.jna.platform.win32.Advapi32Util
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate

class HubSpotClientTest {
    val hubspotClient: HubSpotClient = HubSpotClient(
        ObjectMapper(), HubSpotProperties(),
        RestTemplate()
    )

    @Test
    fun `does not unsubscribe when opted into marketing`() {
        val hubspotClientMock: HubSpotClient = spy(hubspotClient)

        val optedInUser = UserFactory.sample(user = AccountFactory.sample(hasOptedIntoMarketing = true))

        hubspotClientMock.update(listOf(optedInUser))

        verify(hubspotClientMock, never()).unsubscribeFromAll(any())
    }

    @Test
    fun `unsubscribes when opted out of marketing`() {
        val hubspotClientMock: HubSpotClient = spy(hubspotClient)

        val optedInUser = UserFactory.sample(user = AccountFactory.sample(hasOptedIntoMarketing = false))

        hubspotClientMock.update(listOf(optedInUser))

        verify(hubspotClientMock, times(1)).unsubscribeFromAll(any())
    }
}