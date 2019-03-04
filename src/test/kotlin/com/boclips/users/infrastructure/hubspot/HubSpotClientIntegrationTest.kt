package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.User
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.AccountFactory
import com.boclips.users.testsupport.UserFactory
import com.boclips.users.testsupport.UserIdentityFactory
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
class HubSpotClientIntegrationTest : AbstractSpringIntegrationTest() {
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

        val users = listOf(activatedUser())

        hubSpotClient.update(users)

        wireMockServer.verify(
            postRequestedFor(urlMatching(".*/contacts/v1/contact/batch.*"))
                .withQueryParam("hapikey", matching("some-api-key"))
                .withRequestBody(equalToJson(loadJsonFile("hubspot-one-contact.json")))
                .withHeader("Content-Type", matching(MediaType.APPLICATION_JSON_UTF8_VALUE))
        )
    }

    private fun loadJsonFile(fileName: String): String? {
        return IOUtils.toString(
            ResourceUtils.getFile("classpath:wiremock/$fileName").toURI(),
            Charset.defaultCharset()
        )
    }

    private fun activatedUser(): User {
        return UserFactory.sample(
            account = AccountFactory.sample(activated = true),
            identity = UserIdentityFactory.sample(
                email = "someuser@boclips.com",
                firstName = "Ben",
                lastName = "Huang"
            )
        )
    }

    private fun setUpHubSpotStub() {
        wireMockServer.stubFor(
            post(urlPathEqualTo("/contacts/v1/contact/batch"))
                .willReturn(aResponse().withStatus(202))
        )
    }
}