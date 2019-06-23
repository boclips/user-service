package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.CrmProfile
import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.infrastructure.getContentTypeHeader
import com.boclips.users.infrastructure.hubspot.resources.HubSpotProperties
import com.boclips.users.infrastructure.hubspot.resources.SubscriptionStatus
import com.boclips.users.infrastructure.hubspot.resources.UnsubscribeFromMarketingEmails
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.springframework.http.HttpEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

class HubSpotClient(
    val objectMapper: ObjectMapper,
    val hubspotProperties: HubSpotProperties,
    val restTemplate: RestTemplate
) : CustomerManagementProvider {
    companion object : KLogging()

    override fun update(crmProfiles: List<CrmProfile>) {
        try {
            val allValidContacts = crmProfiles.filter { it.isValid() }

            allValidContacts
                .windowed(hubspotProperties.batchSize, hubspotProperties.batchSize, true)
                .forEachIndexed { index, batchOfUsers ->
                    val contacts = updateContacts(batchOfUsers)
                    logger.info { "[Batch $index]: synced ${contacts.size} users with HubSpot" }
                }

            logger.info { "Successfully synchronized ${allValidContacts.size} contacts with HubSpot" }
        } catch (ex: Exception) {
            logger.error { "Could not update some users as a contact on HubSpot. Reason: $ex" }
        }
    }

    override fun unsubscribe(crmProfile: CrmProfile) {
        try {
            restTemplate.put(
                getEmailEndPointForUser(crmProfile.email),
                UnsubscribeFromMarketingEmails(
                    subscriptionStatuses = listOf(SubscriptionStatus(id = hubspotProperties.marketingSubscriptionId))
                )
            )
        } catch (ex: Exception) {
            logger.info { "Could not unsubscribe contact ${crmProfile.id}" }
        }
    }

    private fun updateContacts(batchOfUsers: List<CrmProfile>): List<HubSpotContact> {
        val contacts = batchOfUsers.map { crmProfile ->
            if (!crmProfile.hasOptedIntoMarketing) {
                unsubscribe(crmProfile)
            }

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

    private fun getEmailEndPointForUser(email: String): URI {
        return UriComponentsBuilder.fromUriString("${hubspotProperties.host}/email/public/v1/subscriptions/$email")
            .queryParam("hapikey", hubspotProperties.apiKey)
            .build()
            .toUri()
    }
}