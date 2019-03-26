package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.User
import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.infrastructure.getContentTypeHeader
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.springframework.http.HttpEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

class HubSpotClient(
    val objectMapper: ObjectMapper,
    val hubspotProperties: HubSpotProperties
) : CustomerManagementProvider {
    companion object : KLogging()

    private val restTemplate = RestTemplate()

    override fun update(users: List<User>) {
        try {
            logger.info { "Sychronising contacts with HubSpot" }
            users
                .windowed(hubspotProperties.batchSize, hubspotProperties.batchSize, true)
                .forEachIndexed { index, batchOfUsers ->
                    val contacts = batchOfUsers.map { user ->
                        toHubSpotContact(user)
                    }

                    if (contacts.isNotEmpty()) {
                        postContacts(contacts)
                    }

                    logger.info { "[Batch $index]: synced ${contacts.size} users with HubSpot" }
                }
            logger.info { "Successfully synchronized all valid contacts with HubSpot" }
        } catch (ex: Exception) {
            logger.error { "Could not update user $users as a contact on HubSpot" }
        }
    }

    private fun toHubSpotContact(user: User): HubSpotContact {
        return HubSpotContact(
            email = user.email,
            properties = listOfNotNull(
                HubSpotProperty("firstname", user.firstName),
                HubSpotProperty("lastname", user.lastName),
                HubSpotProperty("is_b2t", "true"),
                HubSpotProperty("b2t_is_activated", user.activated.toString())
            )
        )
    }

    private fun postContacts(contacts: List<HubSpotContact>) {
        val entity = HttpEntity(objectMapper.writeValueAsString(contacts), getContentTypeHeader())
        restTemplate.postForLocation(
            getContactsEndpoint(),
            entity
        )
    }

    private fun getContactsEndpoint(): URI {
        return UriComponentsBuilder
            .fromUriString("${hubspotProperties.host}/contacts/v1/contact/batch")
            .queryParam("hapikey", hubspotProperties.apiKey)
            .build()
            .toUri()
    }
}