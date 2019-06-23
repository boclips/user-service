package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.service.userToCrmProfile
import com.boclips.users.infrastructure.hubspot.resources.HubSpotProperties
import com.boclips.users.testsupport.AccountFactory
import com.boclips.users.testsupport.UserFactory
import com.boclips.users.testsupport.UserSessionsFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
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

        hubspotClientMock.update(listOf(userToCrmProfile(optedInUser, UserSessionsFactory.sample())))

        verify(hubspotClientMock, never()).unsubscribe(any())
    }

    @Test
    fun `unsubscribes when opted out of marketing`() {
        val hubspotClientMock: HubSpotClient = spy(hubspotClient)

        val optedInUser = UserFactory.sample(user = AccountFactory.sample(hasOptedIntoMarketing = false))

        hubspotClientMock.update(listOf(userToCrmProfile(optedInUser, UserSessionsFactory.sample())))

        verify(hubspotClientMock, times(1)).unsubscribe(any())
    }
}