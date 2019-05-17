package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.service.userToCrmProfile
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.AccountFactory
import com.boclips.users.testsupport.UserFactory
import com.boclips.users.testsupport.loadWireMockStub
import com.boclips.videos.service.client.Subject
import com.boclips.videos.service.client.internal.FakeClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.matching
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.put
import com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

class HubSpotClientIntegrationTest : AbstractSpringIntegrationTest() {

    val fakeVideoServiceClient = FakeClient()

    var hubSpotClient: HubSpotClient = HubSpotClient(
        ObjectMapper(), HubSpotProperties().apply {
            this.host = "http://localhost:9999"
            this.apiKey = "some-api-key"
            this.batchSize = 100
            this.marketingSubscriptionId = 123
        },
        RestTemplate()
    )

    @Test
    fun `updates contacts in hubspot`() {
        setUpHubSpotStub()
        fakeVideoServiceClient.addSubject(Subject.builder().id("1").name("Maths").build())
        fakeVideoServiceClient.addSubject(Subject.builder().id("2").name("Science").build())

        val crmProfiles = listOf(userToCrmProfile(activatedUser()))

        hubSpotClient.update(crmProfiles)

        wireMockServer.verify(
            postRequestedFor(urlMatching(".*/contacts/v1/contact/batch.*"))
                .withQueryParam("hapikey", matching("some-api-key"))
                .withRequestBody(equalToJson(loadWireMockStub("hubspot-one-contact.json")))
                .withHeader("Content-Type", matching(MediaType.APPLICATION_JSON_UTF8_VALUE))
        )
    }

    @Test
    fun `unsubscribes contacts from all emails`() {
        setUpHubSpotStub()

        val optedOutOfMarketingUser = UserFactory.sample(user = AccountFactory.sample(hasOptedIntoMarketing = false))

        hubSpotClient.update(listOf(userToCrmProfile(optedOutOfMarketingUser)))

        wireMockServer.verify(
            putRequestedFor(urlMatching(".*/email/public/v1/subscriptions/${optedOutOfMarketingUser.email}.*"))
                .withQueryParam("hapikey", matching("some-api-key"))
                .withRequestBody(
                    equalToJson(
                        """{
                            "subscriptionStatuses": [
                                {
                                    "id": 123,
                                    "subscribed": false
                                }
                            ]
                        }
                        """.trimIndent()
                    )
                )
        )
    }

    private fun activatedUser(): User {
        return UserFactory.sample(
            user = AccountFactory.sample(
                activated = true,
                firstName = "Jane",
                lastName = "Doe",
                email = "jane@doe.com",
                hasOptedIntoMarketing = true,
                subjects = listOf(
                    com.boclips.users.domain.model.Subject(id = SubjectId("1"), name = "Maths"),
                    com.boclips.users.domain.model.Subject(id = SubjectId("2"), name = "Science")
                ),
                ageRange = listOf(3, 4, 5, 6)
            )

        )
    }

    private fun setUpHubSpotStub() {
        wireMockServer.stubFor(
            post(urlPathEqualTo("/contacts/v1/contact/batch"))
                .willReturn(aResponse().withStatus(202))
        )

        wireMockServer.stubFor(
            put(urlPathEqualTo("/email/public/v1/subscriptions/"))
                .willReturn(aResponse().withStatus(202))
        )
    }
}