package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.users.CustomerManagementProvider
import com.boclips.users.infrastructure.keycloakclient.KeycloakUser
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
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
    companion object : KLogging()

    val restTemplate = RestTemplate()

    override fun update(users: List<KeycloakUser>) {
        logger.info { "Sychronising ${users.size} contacts with HubSpot" }
        users
            .windowed(hubspotProperties.batchSize, hubspotProperties.batchSize, true)
            .forEachIndexed { index, batchOfUsers ->
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

                val entity = HttpEntity(objectMapper.writeValueAsString(contacts), getContentTypeHeader())

                restTemplate.postForLocation(
                    getContactsEndpoint(),
                    entity
                )

                logger.info { "[Batch $index]: synced ${contacts.size} users with HubSpot" }
            }
        logger.info { "Successfully synchronized ${users.size} contacts with HubSpot" }
    }

    private fun getContactsEndpoint(): URI {
        return UriComponentsBuilder
            .fromUriString("${hubspotProperties.host}/contacts/v1/contact/batch")
            .queryParam("hapikey", hubspotProperties.apiKey)
            .build()
            .toUri()
    }

    private fun getContentTypeHeader(): HttpHeaders {
        val headers = HttpHeaders()
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        return headers
    }
}