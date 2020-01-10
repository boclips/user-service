package com.boclips.users.infrastructure.schooldigger

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.account.District
import com.boclips.users.domain.model.account.School
import com.boclips.users.domain.service.AmericanSchoolsProvider
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
                            LookupEntry("id-1", "Abraham Lincoln High School, Brooklyn"),
                            LookupEntry("id-2", "Stella K Abraham High School For Girls, Hewlett")
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
            LookupEntry("id-1", "Abraham Lincoln High School, Brooklyn"),
            LookupEntry("id-2", "Stella K Abraham High School For Girls, Hewlett")
        )
    }
}

@Profile("test")
@Service
class FakeAmericanSchoolsProvider : AmericanSchoolsProvider {

    var calls = 0
    var entries = mutableMapOf<String, List<LookupEntry>>()
    var pair: Pair<School, District?>? = null

    override fun fetchSchool(schoolId: String): Pair<School, District?>? {
        calls++
        return pair
    }

    fun createLookupEntries(stateId: String, vararg lookupEntries: LookupEntry) {
        entries.put(stateId, lookupEntries.toList())
    }

    fun createSchoolAndDistrict(pair: Pair<School, District?>?) {
        this.pair = pair
    }

    override fun lookupSchools(stateId: String, schoolName: String): List<LookupEntry> {
        calls++
        return entries[stateId]?.filter { it.name.contains(schoolName) } ?: emptyList()
    }

    fun callCount() = calls

    fun clear() {
        calls = 0
        pair = null
        entries = mutableMapOf()
    }
}
