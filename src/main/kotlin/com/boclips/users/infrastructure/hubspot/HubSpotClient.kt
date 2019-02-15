package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.users.CustomerManagementProvider
import com.boclips.users.infrastructure.keycloakclient.KeycloakUser
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Service
class HubSpotClient(
    val objectMapper: ObjectMapper,
    val hubspotProperties: HubSpotProperties
) : CustomerManagementProvider {
    val restTemplate = RestTemplate()

    override fun update(users: List<KeycloakUser>) {
        users
            .windowed(hubspotProperties.batchSize, hubspotProperties.batchSize, true)
            .forEach { batchOfUsers ->
                val contacts = batchOfUsers.map { user ->
                    HubSpotContact(
                        email = user.email!!,
                        properties = listOf(
                            HubSpotProperty("firstname", user.firstName!!),
                            HubSpotProperty("lastname", user.lastName!!),
                            HubSpotProperty("is_b2t", "true"),
                            HubSpotProperty("b2t_is_activated", user.isVerified.toString())
                        )
                    )
                }

                val entity = HttpEntity(objectMapper.writeValueAsString(contacts), getHeaders())

                restTemplate.postForLocation(
                    getUri(),
                    entity
                )
            }
    }

    private fun getUri(): URI {
        return UriComponentsBuilder
            .fromUriString("${hubspotProperties.host}/contacts/v1/contact/batch")
            .queryParam("hapikey", hubspotProperties.apiKey)
            .build()
            .toUri()
    }

    private fun getHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        return headers
    }
}