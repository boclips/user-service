package com.boclips.users.infrastructure.schooldigger

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.service.AmericanSchoolsProvider
import com.boclips.users.testsupport.factories.OrganisationFactory
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
                        createSchools(
                            "NY",
                            LookupEntry("id-1", "Abraham Lincoln High School"),
                            LookupEntry("id-2", "Stella K Abraham High School For Girls")
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
            LookupEntry("id-1", "Abraham Lincoln High School"),
            LookupEntry("id-2", "Stella K Abraham High School For Girls")
        )
    }
}

@Profile("test")
@Service
class FakeAmericanSchoolsProvider : AmericanSchoolsProvider {

    var calls = 0

    override fun fetchSchool(schoolId: String): Pair<School, District?>? {
        calls++
        return OrganisationFactory.school(externalId = schoolId) to null
    }

    val entries = mutableMapOf<String, List<LookupEntry>>()

    fun createSchools(stateId: String, vararg lookupEntries: LookupEntry) {
        entries.put(stateId, lookupEntries.toList())
    }

    override fun lookupSchools(stateId: String, schoolName: String): List<LookupEntry> {
        calls++
        return entries[stateId]?.filter { it.name.contains(schoolName) } ?: emptyList()
    }

    fun callCount() = calls

    fun clearCalls() {
        calls = 0
    }
}