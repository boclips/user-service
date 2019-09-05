package com.boclips.users.infrastructure.schooldigger

import com.boclips.users.domain.service.AmericanSchoolsProvider
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.concurrent.ListenableFutureCallback
import org.springframework.web.client.AsyncRestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

class SchoolDiggerClient(
    val properties: SchoolDiggerProperties,
    private val restTemplate: AsyncRestTemplate
) : AmericanSchoolsProvider {
    override fun getSchools(state: String, school: String): List<SchoolResponse> {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)

        val httpEntity = HttpEntity<String>(headers)

        val future = restTemplate.exchange(
            buildSearchSchoolsEndpoint(state, school),
            HttpMethod.GET,
            httpEntity,
            SchoolDiggerMatchesResponse::class.java
        )

        Futures.addCallback(future, FutureCallback<SchoolDiggerMatchesResponse>) {
            fun onSuccess(future) {

            }
        }

        return response?.let { it.schools }
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

    private fun onSuccess(response: SchoolDiggerMatchesResponse?): List<SchoolResponse> {
        return response?.let { it.schools } ?: emptyList()
    }
}