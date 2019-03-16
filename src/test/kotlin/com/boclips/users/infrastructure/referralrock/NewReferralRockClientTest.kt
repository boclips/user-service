package com.boclips.users.infrastructure.referralrock

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.loadWireMockStub
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.matching
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.Base64

class NewReferralRockClientTest : AbstractSpringIntegrationTest() {
    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    private lateinit var referralRockClient: ReferralRockClient

    @BeforeEach
    fun setUp() {
        wireMockServer.resetAll()

        referralRockClient = ReferralRockClient(
            ReferralRockProperties().apply {
                host = "http://localhost:9999"
                publicKey = "public-key"
                privateKey = "private-key"
            },
            objectMapper
        )
    }

    @Test
    fun `create a referral`() {
        wireMockServer.stubFor(
            post(urlEqualTo("/api/referrals"))
                .willReturn(
                    aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(loadWireMockStub("referralrock-created-referral.json"))
                )
        )

        val referralId = referralRockClient.createReferral(
            NewReferral(
                referralCode = "1234567",
                firstName = "Jane",
                lastName = "Doe",
                email = "jane@doe.com",
                externalIdentifier = "35",
                status = "qualified"
            )
        )

        assertThat(referralId.value).isEqualTo("20557f69-b8ff-46c1-81df-7527447f3d41")

        wireMockServer.verify(
            WireMock.postRequestedFor(WireMock.urlMatching(".*/api/referrals"))
                .withRequestBody(WireMock.equalToJson(loadWireMockStub("referralrock-create-referral.json")))
                .withHeader(
                    "Authorization",
                    matching(basicAuth(user = "public-key", password = "private-key"))
                )

        )
    }

    @Test
    fun `update referral status`() {
        // POST api/referrals/status
    }

    private fun basicAuth(user: String, password: String): String {
        val userNamePasswordEncoded = String(Base64.getEncoder().encode("$user:$password".toByteArray()))
        return "Basic $userNamePasswordEncoded"
    }
}