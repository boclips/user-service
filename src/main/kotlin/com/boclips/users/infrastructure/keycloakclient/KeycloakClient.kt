package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.domain.model.users.IdentityProvider.Companion.TEACHERS_GROUP_NAME
import mu.KLogging
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.UserResource
import org.keycloak.representations.idm.UserRepresentation
import java.time.LocalDate

class KeycloakClient(
    private val keycloak: Keycloak
) : IdentityProvider {

    companion object : KLogging() {
        const val REALM = "boclips"
    }

    override fun getUserById(id: String): KeycloakUser {
        val user: UserRepresentation?
        try {
            user = getUserResource(id).toRepresentation()
        } catch (e: javax.ws.rs.NotFoundException) {
            throw ResourceNotFoundException()
        }

        return KeycloakUser.from(user)
    }

    override fun getUserByUsername(username: String): KeycloakUser {
        val user = keycloak.realm(REALM).users().search(username)
            .first { it.username == username }

        return KeycloakUser.from(user)
    }

    override fun hasLoggedIn(id: String): Boolean {
        val events = keycloak.realm(REALM).getEvents(
            listOf("LOGIN"),
            null,
            id,
            null,
            null,
            null,
            null,
            1
        )
        return events.isNotEmpty()
    }

    override fun getNewTeachers(since: LocalDate): List<String> = keycloak.realm(REALM)
        .getAdminEvents(listOf("CREATE"), null, null, null, null, null, since.toString(), null, 0, 9999)
        .map { it.resourcePath.substringAfter("users/").substringBefore("/") }
        .filter { userId ->
            try {
                val groupNames = getUserResource(userId).groups().map { group -> group.name }
                if (groupNames.contains(TEACHERS_GROUP_NAME)) {
                    true
                } else {
                    logger.error { "User $userId not in teachers group" }
                    false
                }
            } catch (e: Exception) {
                logger.error(e) { "Could not find newly created user $userId" }
                false
            }
        }

    override fun getUsers(): List<KeycloakUser> {
        val userCount = keycloak.realm(REALM).users().count()

        return keycloak
            .realm(REALM)
            .users()
            .list(0, userCount)
            .map { KeycloakUser.from(it) }
    }

    private fun getUserResource(id: String): UserResource {
        return keycloak.realm(REALM).users().get(id)
    }
}
