package com.boclips.users.infrastructure.schooldigger

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.loadWireMockStub
import com.github.tomakehurst.wiremock.client.WireMock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

class SchoolDiggerClientTest : AbstractSpringIntegrationTest() {
    lateinit var schoolDiggerClient: SchoolDiggerClient

    @BeforeEach
    fun setUp() {
        wireMockServer.resetAll()

        schoolDiggerClient = SchoolDiggerClient(
            SchoolDiggerProperties().apply {
                host = "http://localhost:9999"
                applicationId = "app-id"
                applicationKey = "app-key"
            },
            RestTemplate()
        )
    }

    @Test
    fun `searches school digger API for schools`() {
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/v1.2/autocomplete/schools?q=Abraham&st=NY&appID=app-id&appKey=app-key"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withBody(loadWireMockStub("schooldigger-get-schools-request.json"))
                )
        )

        schoolDiggerClient.getSchools(state = "NY", school = "Abraham")

        wireMockServer.verify(
            WireMock.getRequestedFor(WireMock.urlMatching("/v1.2/autocomplete/schools?q=Abraham&st=NY&appID=app-id&appKey=app-key"))
        )
    }
}