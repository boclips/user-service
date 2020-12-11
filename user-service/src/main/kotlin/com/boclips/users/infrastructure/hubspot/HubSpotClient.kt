package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.marketing.CrmProfile
import com.boclips.users.domain.service.marketing.MarketingService
import com.boclips.users.infrastructure.getContentTypeHeader
import com.boclips.users.infrastructure.hubspot.resources.HubSpotProperties
import com.boclips.users.infrastructure.hubspot.resources.MarketingEmailSubscriptions
import com.boclips.users.infrastructure.hubspot.resources.SubscriptionStatus
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

class HubSpotClient(
    val objectMapper: ObjectMapper,
    val hubspotProperties: HubSpotProperties,
    val restTemplate: RestTemplate
) : MarketingService {
    companion object : KLogging()

    override fun updateProfile(crmProfiles: List<CrmProfile>) {
        try {
            crmProfiles
                .windowed(hubspotProperties.batchSize, hubspotProperties.batchSize, true)
                .forEachIndexed { index, batchOfUsers ->
                    val contacts = updateContacts(batchOfUsers)
                    logger.info { "[Batch $index]: synced ${contacts.size} users with HubSpot" }
                }

            logger.info { "Successfully synchronized ${crmProfiles.size} contacts with HubSpot" }
        } catch (ex: Exception) {
            logger.error { "Could not update some users as a contact on HubSpot. Reason: $ex" }
        }
    }

    override fun updateSubscription(crmProfile: CrmProfile) {
        try {
            restTemplate.put(
                getEmailEndPointForUser(crmProfile.email),
                MarketingEmailSubscriptions(
                    subscriptionStatuses = listOf(
                        SubscriptionStatus(
                            id = hubspotProperties.marketingSubscriptionId,
                            subscribed = crmProfile.hasOptedIntoMarketing
                        )
                    )
                )
            )
        } catch (ex: Exception) {
            logger.info { "Could not modify subscriptions of contact ${crmProfile.id}" }
        }
    }

    override fun deleteContact(email: String) {
        val headers = HttpHeaders()
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        logger.info { "Getting hubspot account of $email" }

        val httpEntity = HttpEntity<String>(headers)
        val response: ResponseEntity<RetrievedHubSpotContact>? = try {
            restTemplate.exchange(
                getContactByEmailEndpoint(email),
                HttpMethod.GET,
                httpEntity,
                RetrievedHubSpotContact::class.java
            )
        } catch (ex: Exception) {
            logger.error("contact not found for email $email")
            return
        }

        response?.body?.vid?.let {
            logger.info { "resolved $email to vid: ${response.body?.vid} " }
            try {
                restTemplate.delete(
                    getDeleteContactEndpoint(it)
                )
                logger.info { "deleting hubspot contact with vid $it " }
            } catch (ex: Exception) {
                logger.error("cannot delete contact with vid $it in hubspot", ex)
            }
        } ?: logger.info { "vid not found for $email" }
    }

    private fun updateContacts(batchOfUsers: List<CrmProfile>): List<HubSpotContact> {
        val contacts = batchOfUsers.map { crmProfile ->
            return@map HubSpotContactConverter().convert(crmProfile)
        }

        postContacts(contacts)

        return contacts
    }

    private fun postContacts(contacts: List<HubSpotContact>) {
        if (contacts.isEmpty()) {
            return
        }

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

    private fun getDeleteContactEndpoint(id: String): URI {
        return UriComponentsBuilder
            .fromUriString("${hubspotProperties.host}/contacts/v1/contact/vid/${id}")
            .queryParam("hapikey", hubspotProperties.apiKey)
            .build()
            .toUri()
    }

    private fun getContactByEmailEndpoint(email: String): URI {
        return UriComponentsBuilder
            .fromUriString("${hubspotProperties.host}/contacts/v1/contact/email/${email}/profile")
            .queryParam("hapikey", hubspotProperties.apiKey)
            .build()
            .toUri()
    }

    private fun getEmailEndPointForUser(email: String): URI {
        return UriComponentsBuilder.fromUriString("${hubspotProperties.host}/email/public/v1/subscriptions/$email")
            .queryParam("hapikey", hubspotProperties.apiKey)
            .build()
            .toUri()
    }
}
