package com.boclips.users.infrastructure.hubspot

import com.boclips.users.infrastructure.keycloakclient.KeycloakUser
import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import com.boclips.users.testsupport.KeycloakUserFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.matching
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.MediaType
import org.springframework.util.ResourceUtils
import java.nio.charset.Charset

@AutoConfigureWireMock(port = 9999)
class HubSpotClientIntegrationTest : AbstractSpringIntergrationTest() {
    @Autowired
    protected lateinit var wireMockServer: WireMockServer

    var hubSpotClient: HubSpotClient = HubSpotClient(
        ObjectMapper(), HubSpotProperties().apply {
            this.host = "http://localhost:9999"
            this.apiKey = "some-api-key"
            this.batchSize = 100
        }
    )

    @BeforeEach
    fun setUp() {
        wireMockServer.resetAll()
    }

    @Test
    fun `updates contacts in hubspot`() {
        setUpHubSpotStub()

        val users = listOf(verifiedUser())

        hubSpotClient.update(users)

        wireMockServer.verify(
            postRequestedFor(urlMatching(".*/contacts/v1/contact/batch.*"))
                .withQueryParam("hapikey", matching("some-api-key"))
                .withRequestBody(equalToJson(loadJsonFile("hubspot-one-contact.json")))
                .withHeader("Content-Type", matching(MediaType.APPLICATION_JSON_VALUE))
        )
    }

    @Test
    fun `does not update invalid emails`() {
        setUpHubSpotStub()

        val users = listOf(
            KeycloakUserFactory.sample(email = "gfgf@fghh.ko"),
            KeycloakUserFactory.sample(email = "aa@aa.aa"),
            KeycloakUserFactory.sample(email = "test@test.test"),
            KeycloakUserFactory.sample(email = "tod@tod.tod")
        )

        hubSpotClient.update(users)

        wireMockServer.verify(0, postRequestedFor(urlMatching(".*/contacts/v1/contact/batch.*")))
    }

    @Test
    fun `omits first and lastname if not available`() {
        setUpHubSpotStub()

        val users = listOf(userWithNoName())

        hubSpotClient.update(users)

        wireMockServer.verify(
            postRequestedFor(urlMatching(".*/contacts/v1/contact/batch.*"))
                .withQueryParam("hapikey", matching("some-api-key"))
                .withRequestBody(equalToJson(loadJsonFile("hubspot-no-names-contact.json")))
                .withHeader("Content-Type", matching(MediaType.APPLICATION_JSON_VALUE))
        )
    }

    private fun loadJsonFile(fileName: String): String? {
        return IOUtils.toString(
            ResourceUtils.getFile("classpath:wiremock/$fileName").toURI(),
            Charset.defaultCharset()
        )
    }

    private fun verifiedUser(): KeycloakUser {
        return KeycloakUserFactory.sample(
            email = "someuser@boclips.com",
            firstName = "Ben",
            lastName = "Huang",
            isVerified = true
        )
    }

    private fun userWithNoName(): KeycloakUser {
        return KeycloakUserFactory.sample(
            email = "someuser@boclips.com",
            firstName = null,
            lastName = null,
            isVerified = true
        )
    }

    private fun setUpHubSpotStub() {
        wireMockServer.stubFor(
            post(urlPathEqualTo("/contacts/v1/contact/batch"))
                .willReturn(
                    aResponse()
                        .withStatus(202)
                )
        )
    }
}