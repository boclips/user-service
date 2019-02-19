package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.service.CustomerManagementProvider
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.apache.commons.validator.routines.EmailValidator
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

class HubSpotClient(
    val objectMapper: ObjectMapper,
    val hubspotProperties: HubSpotProperties
) : CustomerManagementProvider {
    companion object : KLogging()

    private val restTemplate = RestTemplate()
    private val emailValidator = EmailValidator.getInstance()

    override fun update(users: List<Identity>) {
        logger.info { "Sychronising contacts with HubSpot" }
        users
            .windowed(hubspotProperties.batchSize, hubspotProperties.batchSize, true)
            .forEachIndexed { index, batchOfUsers ->
                val contacts = batchOfUsers.mapNotNull { user ->
                    if (!emailValidator.isValid(user.email)) {
                        logger.warn { "Not synchronizing user ${user.id} as email is not set" }
                        return@mapNotNull null
                    }

                    toHubSpotContact(user)
                }

                val entity = HttpEntity(objectMapper.writeValueAsString(contacts), getContentTypeHeader())

                if (contacts.isNotEmpty()) {
                    restTemplate.postForLocation(
                        getContactsEndpoint(),
                        entity
                    )
                }

                logger.info { "[Batch $index]: synced ${contacts.size} users with HubSpot" }
            }
        logger.info { "Successfully synchronized all valid contacts with HubSpot" }
    }

    private fun toHubSpotContact(user: Identity): HubSpotContact {
        return HubSpotContact(
            email = user.email,
            properties = listOfNotNull(
                HubSpotProperty("firstname", user.firstName),
                HubSpotProperty("lastname", user.lastName),
                HubSpotProperty("is_b2t", "true"),
                HubSpotProperty("b2t_is_activated", user.isVerified.toString())
            )
        )
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
        headers.set("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        return headers
    }
}