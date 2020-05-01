package com.boclips.users.infrastructure.schooldigger

import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.ExternalOrganisationInformation
import com.boclips.users.domain.model.organisation.ExternalSchoolInformation
import com.boclips.users.domain.service.organisation.AmericanSchoolsProvider
import com.boclips.users.testsupport.loadWireMockStub
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.stream.Stream

class AmericanSchoolsProviderContractTest {

    class AmericanSchoolsProviders : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    SchoolDiggerClient(
                        SchoolDiggerProperties().apply
                        {
                            host = "http://localhost:9998"
                            applicationId = "app-id"
                            applicationKey = "app-key"
                        },
                        RestTemplate()
                    ),
                    {
                        val wireMockServer = WireMockServer(9998).apply {
                            start()
                        }
                        wireMockServer.resetAll()

                        wireMockServer.stubFor(
                            WireMock.get(WireMock.urlEqualTo("/v1.2/autocomplete/schools?q=Abraham&st=NY&appID=app-id&appKey=app-key"))
                                .willReturn(
                                    WireMock.aResponse()
                                        .withStatus(200)
                                        .withHeader("content-type", "application/json")
                                        .withBody(loadWireMockStub("schooldigger-get-schools-request.json"))
                                )
                        )
                    }),

                Arguments.of(
                    FakeAmericanSchoolsProvider().apply {
                        createLookupEntries(
                            "NY",
                            ExternalOrganisationInformation(ExternalOrganisationId("id-1"), "Abraham Lincoln High School, Brooklyn", Address()),
                            ExternalOrganisationInformation(ExternalOrganisationId("id-2"), "Stella K Abraham High School For Girls, Hewlett", Address())
                        )
                    },
                    {})
            )
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AmericanSchoolsProviders::class)
    fun `searches school digger API for schools`(client: AmericanSchoolsProvider, setup: () -> Void) {
        setup()

        val schools = client.lookupSchools(stateId = "NY", schoolName = "Abraham")

        assertThat(schools).containsExactly(
            ExternalOrganisationInformation(ExternalOrganisationId("id-1"), "Abraham Lincoln High School, Brooklyn", Address()),
            ExternalOrganisationInformation(ExternalOrganisationId("id-2"), "Stella K Abraham High School For Girls, Hewlett", Address())
        )
    }
}

@Profile("test")
@Service
class FakeAmericanSchoolsProvider :
    AmericanSchoolsProvider {

    var calls = 0
    var entries = mutableMapOf<String, List<ExternalOrganisationInformation>>()
    var schoolDetails: ExternalSchoolInformation? = null

    override fun fetchSchool(schoolId: String): ExternalSchoolInformation? {
        calls++
        return schoolDetails
    }

    fun createLookupEntries(stateId: String, vararg lookupEntries: ExternalOrganisationInformation) {
        entries.put(stateId, lookupEntries.toList())
    }

    fun createSchoolAndDistrict(schoolDetails: ExternalSchoolInformation?) {
        this.schoolDetails = schoolDetails
    }

    override fun lookupSchools(stateId: String, schoolName: String): List<ExternalOrganisationInformation> {
        calls++
        return entries[stateId]?.filter { it.name.contains(schoolName) } ?: emptyList()
    }

    fun callCount() = calls

    fun clear() {
        calls = 0
        schoolDetails = null
        entries = mutableMapOf()
    }
}
