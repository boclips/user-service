package com.boclips.users.domain.service

import com.boclips.users.domain.model.events.AnalyticsClient
import com.boclips.users.domain.model.events.Event
import com.boclips.users.domain.model.events.EventType
import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.domain.model.users.KeycloakId
import com.boclips.users.infrastructure.keycloakclient.ResourceNotFoundException
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class UserService(
    val identityProvider: IdentityProvider,
    val analyticsClient: AnalyticsClient
) {
    companion object : KLogging()

    @Synchronized
    fun registerUserIfNew(id: String) {
        val userFromKeycloak = identityProvider.getUserById(KeycloakId(value = id))

        if (!userFromKeycloak.activated) {
            analyticsClient.track(Event(eventType = EventType.ACCOUNT_CREATED, userId = id))
            logger.info { "Registered new user: $id" }
        }
    }

    fun findById(id: String) = try {
        identityProvider.getUserById(KeycloakId(value = id))
    } catch (e: ResourceNotFoundException) {
        logger.warn(e) { "Could not find user with id: $id" }
        null
    }
}