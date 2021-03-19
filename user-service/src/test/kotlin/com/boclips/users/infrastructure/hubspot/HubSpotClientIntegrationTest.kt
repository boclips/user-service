package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.subject.Subject
import com.boclips.users.domain.model.subject.SubjectId
import com.boclips.users.domain.model.user.TeacherPlatformAttributes
import com.boclips.users.domain.service.marketing.convertUserToCrmProfile
import com.boclips.users.infrastructure.hubspot.resources.HubSpotProperties
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.*
import com.boclips.users.testsupport.loadWireMockStub
import com.boclips.videos.api.response.subject.SubjectResource
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import java.time.Instant
import java.time.ZonedDateTime

class HubSpotClientIntegrationTest : AbstractSpringIntegrationTest() {

    var hubSpotClient: HubSpotClient = HubSpotClient(
        ObjectMapper(), HubSpotProperties().apply {
        host = "http://localhost:9999"
        apiKey = "some-api-key"
        batchSize = 100
        marketingSubscriptionId = 123
    },
        RestTemplate()
    )

    @Test
    fun `updates contacts in hubspot`() {
        setUpHubSpotStub()

        subjectsClient.add(SubjectResource(id = "1", name = "Maths"))
        subjectsClient.add(SubjectResource(id = "2", name = "Science"))

        val crmProfiles = listOf(
            convertUserToCrmProfile(
                UserFactory.sample(
                    profile = ProfileFactory.sample(
                        subjects = listOf(
                            Subject(
                                id = SubjectId(
                                    "1"
                                ), name = "Maths"
                            ),
                            Subject(
                                id = SubjectId(
                                    "2"
                                ), name = "Science"
                            )
                        ),
                        ages = listOf(3, 4, 5, 6),
                        firstName = "Jane",
                        lastName = "Doe",
                        hasOptedIntoMarketing = true
                    ),
                    identity = IdentityFactory.sample(
                        username = "jane@doe.com"
                    ),
                    teacherPlatformAttributes = TeacherPlatformAttributes(
                        shareCode = "test",
                        hasLifetimeAccess = false
                    ),
                    marketing = MarketingTrackingFactory.sample(
                        utmContent = "utm-content-1",
                        utmTerm = "utm-term-1",
                        utmMedium = "utm-medium-1",
                        utmSource = "utm-source-1",
                        utmCampaign = "utm-campaign-1"
                    ),
                    accessExpiresOn = ZonedDateTime.parse("2017-08-08T00:00:00Z")
                ),
                UserSessionsFactory.sample(lastAccess = Instant.parse("2017-08-08T00:00:00Z"))
            )!!
        )

        hubSpotClient.updateProfile(crmProfiles)

        wireMockServer.verify(
            postRequestedFor(urlMatching(".*/contacts/v1/contact/batch.*"))
                .withQueryParam("hapikey", matching("some-api-key"))
                .withRequestBody(equalToJson(loadWireMockStub("hubspot-one-contact.json")))
                .withHeader("Content-Type", matching(MediaType.APPLICATION_JSON_VALUE))
        )
    }

    @Test
    fun `a contact has opted out of marketing emails`() {
        setUpHubSpotStub()

        val user = UserFactory.sample(profile = ProfileFactory.sample(hasOptedIntoMarketing = false))

        hubSpotClient.updateSubscription(
            convertUserToCrmProfile(
                user,
                UserSessionsFactory.sample()
            )!!
        )

        wireMockServer.verify(
            putRequestedFor(urlMatching(".*/email/public/v1/subscriptions/${user.identity.email}.*"))
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

    @Test
    fun `a contact has opted in to marketing emails`() {
        setUpHubSpotStub()

        val user = UserFactory.sample(profile = ProfileFactory.sample(hasOptedIntoMarketing = true))

        hubSpotClient.updateSubscription(
            convertUserToCrmProfile(
                user,
                UserSessionsFactory.sample()
            )!!
        )

        wireMockServer.verify(
            putRequestedFor(urlMatching(".*/email/public/v1/subscriptions/${user.identity.email}.*"))
                .withQueryParam("hapikey", matching("some-api-key"))
                .withRequestBody(
                    equalToJson(
                        """{
                            "subscriptionStatuses": [
                                {
                                    "id": 123,
                                    "subscribed": true
                                }
                            ]
                        }
                        """.trimIndent()
                    )
                )
        )
    }

    @Test
    fun `a contact can be deleted`() {
        val user = UserFactory.sample(
            profile = ProfileFactory.sample(hasOptedIntoMarketing = true),
            identity = IdentityFactory.sample(username = "boo@baa.com")
        )

        wireMockServer.stubFor(
            get(
                urlPathEqualTo("/contacts/v1/contact/email/boo@baa.com/profile")
            ).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(
                    """
                        {
                        "email": "${user.identity.email}",
                        "properties": [],
                        "vid": "retrieved-users-vid"
                        }
                    """.trimIndent()
                )
            )
        )
        wireMockServer.stubFor(
            delete(
                urlPathEqualTo("/contacts/v1/contact/vid/retrieved-users-vid")
            )
                .willReturn(aResponse().withStatus(200))
        )

        hubSpotClient.deleteContact(
            email = user.identity.email!!
        )

        wireMockServer.verify(
            getRequestedFor(
                urlMatching(".*/contacts/v1/contact/email/boo@baa.com/profile.*")
            ).withQueryParam("hapikey", matching("some-api-key"))
        )
        wireMockServer.verify(
            deleteRequestedFor(
                urlMatching(".*/contacts/v1/contact/vid/retrieved-users-vid.*")
            ).withQueryParam("hapikey", matching("some-api-key"))
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
