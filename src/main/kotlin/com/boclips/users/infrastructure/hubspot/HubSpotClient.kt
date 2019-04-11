package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.User
import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.infrastructure.getContentTypeHeader
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.apache.commons.validator.routines.EmailValidator
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

    override fun update(users: List<User>) {
        try {
            logger.info { "Sychronising contacts with HubSpot" }
            val allValidContacts = users.filter { isRealUser(it) }

            allValidContacts
                .windowed(hubspotProperties.batchSize, hubspotProperties.batchSize, true)
                .forEachIndexed { index, batchOfUsers ->
                    val contacts = batchOfUsers.map { user ->
                        toHubSpotContact(user).also {
                            if (!user.hasOptedIntoMarketing) {
                                unsubscribeFromMarketingEmails(user)
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

    private fun isRealUser(anyUser: User) =
        anyUser.firstName.isNotEmpty() &&
            anyUser.lastName.isNotEmpty() &&
            anyUser.email.isNotEmpty() &&
            EmailValidator.getInstance().isValid(anyUser.email)

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

    private fun getEmailEndPointForUser(user: User): URI =
        UriComponentsBuilder.fromUriString("${hubspotProperties.host}/email/public/v1/subscriptions/${user.email}")
            .queryParam("hapikey", hubspotProperties.apiKey)
            .build()
            .toUri()

    fun unsubscribeFromMarketingEmails(user: User) {
        restTemplate.put(
            getEmailEndPointForUser(user),
            UnsubscribeFromMarketingEmails(
                listOf(SubscriptionStatus(id = hubspotProperties.marketingSubscriptionId))
            )
        )
    }
}

class UnsubscribeFromMarketingEmails(val subscriptionStatuses: List<SubscriptionStatus>)
class SubscriptionStatus(val id: Long, val subscribed: Boolean = false)