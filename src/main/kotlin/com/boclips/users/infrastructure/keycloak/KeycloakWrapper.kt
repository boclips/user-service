package com.boclips.users.infrastructure.keycloak

import mu.KLogging
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.EventRepresentation
import org.keycloak.representations.idm.UserRepresentation
import javax.ws.rs.core.Response

open class KeycloakWrapper(private val keycloak: Keycloak) {
    companion object : KLogging() {
        const val REALM = "boclips"
    }

    fun users(): List<UserRepresentation> =
        keycloak
            .realm(REALM)
            .users()
            .list(0, countUsers())

    fun countUsers(): Int = keycloak.realm(REALM).users().count()

    fun getUser(id: String): UserRepresentation? {
        return try {
            keycloak.realm(REALM).users().get(id).toRepresentation()
        } catch (ex: Exception) {
            logger.info { "Could not find user: $id" }
            null
        }
    }

    fun getLastUserSession(id: String): List<EventRepresentation> {
        return try {
            return keycloak.realm(REALM).getEvents(listOf("LOGIN"), null, id, null, null, null, null, 5)
        } catch (ex: Exception) {
            emptyList()
        }
    }

    fun getUserByUsername(username: String): UserRepresentation? {
        val usernameLowercase = username.toLowerCase()
        val search = keycloak.realm(REALM).users().search(usernameLowercase)

        return search.firstOrNull { it.username == usernameLowercase }
    }

    fun createUser(keycloakUser: KeycloakUser): UserRepresentation {
        logger.info { "Attempt to create user" }
        val response: Response = keycloak.realm(REALM)
            .users()
            .create(UserRepresentation().apply {
                username = keycloakUser.email
                email = keycloakUser.email
                firstName = keycloakUser.firstName
                lastName = keycloakUser.lastName
                credentials = listOf(CredentialRepresentation().apply {
                    type = CredentialRepresentation.PASSWORD
                    value = keycloakUser.password
                    isTemporary = false
                })
                isEmailVerified = false
                isEnabled = true
            })

        if (response.status == 409) throw UserAlreadyExistsException()

        if (response.status != 201) throw UserNotCreatedException("User could not be created, Keycloak returned ${response.status}")

        return getUserByUsername(keycloakUser.email)
            ?: throw UserNotCreatedException("User was created but could not be found.")
    }

    fun removeUser(id: String?) {
        keycloak.realm(REALM).users().delete(id)
    }
}
