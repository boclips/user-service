package com.boclips.users.infrastructure.hubspot

import com.boclips.users.infrastructure.keycloakclient.KeycloakUser
import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import com.boclips.users.testsupport.KeycloakUserFactory
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.matching
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import org.apache.commons.io.IOUtils
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

    @Autowired
    lateinit var hubSpotClient: HubSpotClient

    @Test
    fun `updates contacts in hubspot`() {
        setUpHubSpotStub()

        val users = listOf(verifiedUser())

        hubSpotClient.update(users)

        wireMockServer.verify(
            postRequestedFor(urlMatching(".*/contacts/v1/contact/batch.*"))
                .withQueryParam("hapikey", matching("some-api-key"))
                .withRequestBody(equalToJson(loadActualPayload()))
                .withHeader("Content-Type", matching(MediaType.APPLICATION_JSON_VALUE))
        )
    }

    private fun loadActualPayload(): String? {
        val jsonPayload = IOUtils.toString(
            ResourceUtils.getFile("classpath:wiremock/hubspot-one-contact.json").toURI(),
            Charset.defaultCharset()
        )
        return jsonPayload
    }

    private fun verifiedUser(): KeycloakUser {
        val user = KeycloakUserFactory.sample(
            email = "someuser@boclips.com",
            firstName = "Ben",
            lastName = "Huang",
            isVerified = true
        )
        return user
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