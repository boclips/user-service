package com.boclips.users.infrastructure.schooldigger

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.service.AmericanSchoolsProvider
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

class SchoolDiggerClient(
    val properties: SchoolDiggerProperties,
    private val restTemplate: RestTemplate
) : AmericanSchoolsProvider {
    override fun lookupSchools(stateId: String, schoolName: String): List<LookupEntry> {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)

        val httpEntity = HttpEntity<String>(HttpHeaders())

        val response = restTemplate.exchange(
            buildSearchSchoolsEndpoint(stateId, schoolName),
            HttpMethod.GET,
            httpEntity,
            SchoolDiggerMatchesResponse::class.java
        )

        return response.body?.schoolMatches
            ?.map { LookupEntry(it.schoolid, it.schoolName) }
            ?: emptyList()
    }

    private fun buildSearchSchoolsEndpoint(state: String, school: String): URI {
        return UriComponentsBuilder
            .fromUriString("${properties.host}/v1.2/autocomplete/schools")
            .queryParam("q", school)
            .queryParam("st", state)
            .queryParam("appID", properties.applicationId)
            .queryParam("appKey", properties.applicationKey)
            .build()
            .toUri()
    }
}