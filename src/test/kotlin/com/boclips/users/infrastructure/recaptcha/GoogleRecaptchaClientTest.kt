package com.boclips.users.infrastructure.recaptcha

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.loadWireMockStub
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GoogleRecaptchaClientTest : AbstractSpringIntegrationTest() {

    private lateinit var googleRecaptchaClient: GoogleRecaptchaClient

    @BeforeEach
    fun setUp() {
        wireMockServer.resetAll()

        googleRecaptchaClient = GoogleRecaptchaClient(
            GoogleRecaptchaProperties().apply {
                host = "http://localhost:9999"
                secretKey = "thisisasecret"
                threshold = 0.5
            }
        )
    }

    @Test
    fun `will verify a token with score higher than the threshold`() {
        wireMockServer.stubFor(
            WireMock.post(WireMock.urlPathEqualTo("/recaptcha/api/siteverify"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(loadWireMockStub("recaptcha-successful-score-0.9.json"))
                )
        )

        val response = googleRecaptchaClient.validateCaptchaToken("SomeToken", "AnIdentity")

        assertThat(response).isTrue()

        wireMockServer.verify(
            WireMock.postRequestedFor(WireMock.urlPathEqualTo("/recaptcha/api/siteverify"))
                .withQueryParam("secret", EqualToPattern("thisisasecret"))
                .withQueryParam("response", EqualToPattern("SomeToken"))
        )
    }

    @Test
    fun `does not verify a token with score lower than the threshold`() {
        wireMockServer.stubFor(
            WireMock.post(WireMock.urlPathEqualTo("/recaptcha/api/siteverify"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(loadWireMockStub("recaptcha-successful-score-0.1.json"))
                )
        )

        val response = googleRecaptchaClient.validateCaptchaToken("SomeToken", "AnIdentity")

        assertThat(response).isFalse()

        wireMockServer.verify(
            WireMock.postRequestedFor(WireMock.urlPathEqualTo("/recaptcha/api/siteverify"))
                .withQueryParam("secret", EqualToPattern("thisisasecret"))
                .withQueryParam("response", EqualToPattern("SomeToken"))
        )
    }

    @Test
    fun `fails to verify when the secret key is invalid`() {
        googleRecaptchaClient = GoogleRecaptchaClient(
            GoogleRecaptchaProperties().apply {
                host = "http://localhost:9999"
                secretKey = ""
            }
        )

        wireMockServer.stubFor(
            WireMock.post(WireMock.urlPathEqualTo("/recaptcha/api/siteverify"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(loadWireMockStub("recaptcha-failure-invalid-secret.json"))
                )
        )

        assertThat(googleRecaptchaClient.validateCaptchaToken("SomeToken", "AnIdentity")).isFalse()

        wireMockServer.verify(
            WireMock.postRequestedFor(WireMock.urlPathEqualTo("/recaptcha/api/siteverify"))
                .withQueryParam("secret", EqualToPattern(""))
                .withQueryParam("response", EqualToPattern("SomeToken"))
        )
    }

    @Test
    fun `fails to verify when the token is invalid`() {
        wireMockServer.stubFor(
            WireMock.post(WireMock.urlPathEqualTo("/recaptcha/api/siteverify"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(loadWireMockStub("recaptcha-failure-invalid-response.json"))
                )
        )

        assertThat(googleRecaptchaClient.validateCaptchaToken("", "AnIdentity")).isFalse()

        wireMockServer.verify(
            WireMock.postRequestedFor(WireMock.urlPathEqualTo("/recaptcha/api/siteverify"))
                .withQueryParam("secret", EqualToPattern("thisisasecret"))
                .withQueryParam("response", EqualToPattern(""))
        )
    }

    @Test
    fun `fails to verify when the token has already been verified`() {
        wireMockServer.stubFor(
            WireMock.post(WireMock.urlPathEqualTo("/recaptcha/api/siteverify"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(loadWireMockStub("recaptcha-failure-timeout-duplicate.json"))
                )
        )

        assertThat(googleRecaptchaClient.validateCaptchaToken("SomeToken", "AnIdentity")).isFalse()

        wireMockServer.verify(
            WireMock.postRequestedFor(WireMock.urlPathEqualTo("/recaptcha/api/siteverify"))
                .withQueryParam("secret", EqualToPattern("thisisasecret"))
                .withQueryParam("response", EqualToPattern("SomeToken"))
        )
    }
}
