package com.boclips.users.infrastructure.keycloak

import com.boclips.users.infrastructure.keycloak.client.KeycloakDbClient
import mu.KLogging
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.EventRepresentation
import org.keycloak.representations.idm.UserRepresentation
import javax.ws.rs.core.Response

open class KeycloakWrapper(
    private val keycloak: Keycloak,
    private val keycloakDbClient: KeycloakDbClient
) {
    companion object : KLogging() {
        const val REALM = "boclips"
    }

    fun getAllUserIds() = keycloakDbClient.getAllUserIds()

    fun countUsers(): Int {
        return keycloak.realm(REALM).users().count()
    }

    fun getLastUserSession(id: String): List<EventRepresentation> {
        return try {
            return keycloak.realm(REALM).getEvents(listOf("LOGIN"), null, id, null, null, null, null, 5)
        } catch (ex: Exception) {
            emptyList()
        }
    }

    fun getUserById(id: String): UserRepresentation? {
        return try {
            keycloak.realm(REALM).users().get(id).toRepresentation()
                .apply {
                    realmRoles = getRolesOfUser(id)
                }
        } catch (ex: Exception) {
            logger.info { "Could not find user: $id" }
            null
        }
    }

    fun getUserByUsername(username: String): UserRepresentation? {
        val usernameLowercase = username.toLowerCase()
        val search = keycloak.realm(REALM).users().search(usernameLowercase)

        return search.firstOrNull { it.username == usernameLowercase }
            ?.apply {
                realmRoles = getRolesOfUser(this.id)
            }
    }

    fun createUser(request: KeycloakCreateUserRequest): UserRepresentation {
        logger.info { "Attempt to create user" }
        val response: Response = keycloak.realm(REALM)
            .users()
            .create(
                UserRepresentation().apply {
                    username = request.email
                    email = request.email
                    credentials = listOf(
                        CredentialRepresentation().apply {
                            type = CredentialRepresentation.PASSWORD
                            value = request.password
                            isTemporary = request.isPasswordTemporary
                        }
                    )
                    isEmailVerified = false
                    isEnabled = true
                }
            )

        if (response.status == 409) throw UserAlreadyExistsException()

        if (response.status != 201) throw UserNotCreatedException("User could not be created, Keycloak returned ${response.status}")

        return getUserByUsername(request.email)?.let { user ->
            request.role?.let { role -> addRealmRoleToUser(role, user.id) }
            getUserByUsername(request.email)
        } ?: throw UserNotCreatedException("User was created but could not be found.")
    }

    fun removeUser(id: String?) {
        keycloak.realm(REALM).users().delete(id)
    }

    fun addRealmRoleToUser(role: String, userId: String?) {
        keycloak.realm(REALM).users().get(userId).roles().realmLevel().listAvailable()
            .firstOrNull { it.name == role }
            ?.let {
                keycloak.realm(REALM).users().get(userId).roles().realmLevel().add(listOf(it))
            }
    }

    private fun getRolesOfUser(userId: String): List<String> {
        return keycloak.realm(REALM).users().get(userId).roles().realmLevel().listEffective().map { it.name }
    }
}
