package com.boclips.users.infrastructure.keycloak

import com.boclips.users.infrastructure.keycloak.client.KeycloakClient
import mu.KLogging
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.EventRepresentation
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.UserRepresentation
import java.time.LocalDate

class KeycloakWrapper(private val keycloak: Keycloak) {
    companion object : KLogging()

    fun users(): List<UserRepresentation> =
        keycloak
            .realm(KeycloakClient.REALM)
            .users()
            .list(0, countUsers())

    fun countUsers(): Int = keycloak.realm(KeycloakClient.REALM).users().count()

    fun getUser(id: String): UserRepresentation? {
        return try {
            keycloak.realm(KeycloakClient.REALM).users().get(id).toRepresentation()
        } catch (ex: Exception) {
            logger.warn { "Could not find user: $id" }
            null
        }
    }

    fun getUserByUsername(username: String): UserRepresentation? {
        val usernameLowercase = username.toLowerCase()
        val search = keycloak.realm(KeycloakClient.REALM).users().search(usernameLowercase)

        return search.firstOrNull { it.username == usernameLowercase }
    }

    fun getRegisterEvents(since: LocalDate): List<EventRepresentation> =
        keycloak.realm(KeycloakClient.REALM)
            .getEvents(listOf("REGISTER"), null, null, since.toString(), null, null, 0, 9999)

    fun getGroupsOfUser(id: String): List<GroupRepresentation> {
        return keycloak.realm(KeycloakClient.REALM).users().get(id).groups()
    }
}
