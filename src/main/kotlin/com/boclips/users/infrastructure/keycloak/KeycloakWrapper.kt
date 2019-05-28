package com.boclips.users.infrastructure.keycloak

import mu.KLogging
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.EventRepresentation
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.UserRepresentation
import javax.ws.rs.core.Response

open class KeycloakWrapper(private val keycloak: Keycloak) {
    companion object : KLogging() {
        const val REALM = "boclips"
        const val TEACHER_ROLE = "ROLE_TEACHER"
    }

    fun users(): List<UserRepresentation> =
        keycloak
            .realm(REALM)
            .users()
            .list(0, countUsers())

    fun countUsers(): Int = keycloak.realm(REALM).users().count()

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
                realmRoles =
                    keycloak.realm(REALM).users().get(this.id).roles().realmLevel().listEffective().map { it.name }
            }
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

        return getUserByUsername(keycloakUser.email)?.let { user ->
            addRealmRoleToUser(TEACHER_ROLE, user.id)
            getUserByUsername(keycloakUser.email)
        } ?: throw UserNotCreatedException("User was created but could not be found.")
    }

    fun removeUser(id: String?) {
        keycloak.realm(REALM).users().delete(id)
    }

    fun isInGroup(id: String, groupName: String): Boolean {
        try {
            val groupNames = keycloak.realm(REALM).users().get(id).groups().map { it.name }

            if (groupNames.isEmpty()) return false

            return groupNames.contains(groupName)
        } catch (ex: Exception) {
            return false
        }
    }

    fun removeFromGroup(id: String, groupName: String) {
        val toBeRemovedFromGroup = findGroup(groupName)
        keycloak.realm(REALM).users().get(id).leaveGroup(toBeRemovedFromGroup!!.id)
    }

    fun addToGroup(id: String, groupName: String) {
        val toBeAddedToGroup = findGroup(groupName)
        keycloak.realm(REALM).users().get(id).joinGroup(toBeAddedToGroup!!.id)
    }

    fun addRealmRoleToUser(role: String, userId: String?) {
        keycloak.realm(REALM).users().get(userId).roles().realmLevel().listAvailable()
            .firstOrNull { it.name == role }
            ?.let {
                keycloak.realm(REALM).users().get(userId).roles().realmLevel().add(listOf(it))
            }
    }

    private fun findGroup(groupName: String): GroupRepresentation? {
        return keycloak.realm(REALM).groups().groups()
            .first { groupRepresentation -> groupRepresentation.name == groupName }
    }
}
