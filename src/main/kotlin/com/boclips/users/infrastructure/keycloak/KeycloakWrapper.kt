package com.boclips.users.infrastructure.keycloak

import com.boclips.users.infrastructure.keycloak.client.KeycloakClient
import mu.KLogging
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
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

    fun createUser(keycloakUser: KeycloakUser): UserRepresentation {
        val response = keycloak.realm(KeycloakClient.REALM)
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

        val newUser = searchUsers(keycloakUser.email)
        if (newUser.size != 1) throw UserNotCreatedException()
        return newUser[0]
    }

    fun sendVerificationEmail(id: String) {
        keycloak.realm(KeycloakClient.REALM).users().get(id).sendVerifyEmail()
    }

    fun removeUser(id: String?) {
        keycloak.realm(KeycloakClient.REALM).users().delete(id)
    }

    private fun searchUsers(email: String): List<UserRepresentation> {
        return keycloak.realm(KeycloakClient.REALM)
            .users()
            .search(email)
    }
}
