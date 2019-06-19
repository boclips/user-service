package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.CrmProfile
import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.infrastructure.getContentTypeHeader
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.apache.commons.validator.routines.EmailValidator
import org.springframework.http.HttpEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.LocalDateTime
import java.time.ZoneId

class HubSpotClient(
    val objectMapper: ObjectMapper,
    val hubspotProperties: HubSpotProperties,
    val restTemplate: RestTemplate
) : CustomerManagementProvider {
    companion object : KLogging()

    override fun update(crmProfiles: List<CrmProfile>) {
        try {
            logger.info { "Sychronising contacts with HubSpot" }
            val allValidContacts = crmProfiles.filter { isRealUser(it) }

            allValidContacts
                .windowed(hubspotProperties.batchSize, hubspotProperties.batchSize, true)
                .forEachIndexed { index, batchOfUsers ->
                    val contacts = batchOfUsers.map { crmProfile ->
                        toHubSpotContact(crmProfile).also {
                            if (!crmProfile.hasOptedIntoMarketing) {
                                unsubscribeFromMarketingEmails(crmProfile)
                            }
                        }
                    }

                    if (contacts.isNotEmpty()) {
                        postContacts(contacts)
                    }

                    logger.info { "[Batch $index]: synced ${contacts.size} users with HubSpot" }
                }
            logger.info { "Successfully synchronized ${allValidContacts.size} contacts with HubSpot" }
        } catch (ex: Exception) {
            logger.error { "Could not update some users as a contact on HubSpot. Reason: $ex" }
        }
    }

    fun unsubscribeFromMarketingEmails(crmProfile: CrmProfile) {
        restTemplate.put(
            getEmailEndPointForUser(crmProfile.email),
            UnsubscribeFromMarketingEmails(
                subscriptionStatuses = listOf(SubscriptionStatus(id = hubspotProperties.marketingSubscriptionId))
            )
        )
    }

    private fun isRealUser(anyUser: CrmProfile) =
        anyUser.firstName.isNotEmpty() &&
            anyUser.lastName.isNotEmpty() &&
            anyUser.email.isNotEmpty() &&
            EmailValidator.getInstance().isValid(anyUser.email)

    private fun toHubSpotContact(crmProfile: CrmProfile): HubSpotContact {
        return HubSpotContact(
            email = crmProfile.email,
            properties = listOfNotNull(
                HubSpotProperty("firstname", crmProfile.firstName),
                HubSpotProperty("lastname", crmProfile.lastName),
                HubSpotProperty("is_b2t", "true"),
                HubSpotProperty("b2t_is_activated", crmProfile.activated.toString()),
                HubSpotProperty("subjects_taught", crmProfile.subjects.joinToString { it.name }),
                HubSpotProperty("age_range", crmProfile.ageRange.joinToString()),
                HubSpotProperty("b2t_utm_source", crmProfile.marketingTracking.utmSource),
                HubSpotProperty("b2t_utm_term", crmProfile.marketingTracking.utmTerm),
                HubSpotProperty("b2t_utm_content", crmProfile.marketingTracking.utmContent),
                HubSpotProperty("b2t_utm_medium", crmProfile.marketingTracking.utmMedium),
                HubSpotProperty("b2t_utm_campaign", crmProfile.marketingTracking.utmCampaign),
                HubSpotProperty("b2t_last_logged_in", convertToInstantAtMidnight(crmProfile))
            )
        )
    }

    private fun convertToInstantAtMidnight(crmProfile: CrmProfile): String {
        return crmProfile.lastLoggedIn?.let { lastLogin ->
            LocalDateTime
                .ofInstant(lastLogin, ZoneId.of("UTC"))
                .toLocalDate()
                .atStartOfDay(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli()
                .toString()
        } ?: ""
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

    private fun getEmailEndPointForUser(email: String): URI {
        return UriComponentsBuilder.fromUriString("${hubspotProperties.host}/email/public/v1/subscriptions/$email")
            .queryParam("hapikey", hubspotProperties.apiKey)
            .build()
            .toUri()
    }
}

data class UnsubscribeFromMarketingEmails(val subscriptionStatuses: List<SubscriptionStatus>)
data class SubscriptionStatus(val id: Long, val subscribed: Boolean = false)