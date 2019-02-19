package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.service.IdentityProvider
import mu.KLogging
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.UserResource
import org.keycloak.representations.idm.UserRepresentation
import java.time.LocalDate

class KeycloakClient(
    private val keycloak: Keycloak,
    private val userConverter: KeycloakUserToUserIdentityConverter
) : IdentityProvider {
    companion object : KLogging() {
        const val REALM = "boclips"
        const val TEACHERS_GROUP_NAME: String = "teachers"
    }

    override fun getUserById(id: IdentityId): Identity? {
        val user: UserRepresentation?
        return try {
            user = getUserResource(id.value).toRepresentation()
            userConverter.convert(user)
        } catch (e: javax.ws.rs.NotFoundException) {
            logger.warn { "Could not find user: $id, omitting user" }
            null
        } catch (e: InvalidUserRepresentation) {
            logger.warn { "Could not convert external keycloak user: $id, omitting user" }
            null
        } catch (e: Exception) {
            logger.warn(e) { "Unexpected exception happened when looking up user: $id, omitting user" }
            null
        }
    }

    override fun getUserByUsername(username: String): Identity {
        val usernameLowercase = username.toLowerCase()
        val user = keycloak.realm(REALM).users().search(usernameLowercase)
            .first { it.username == usernameLowercase }

        return userConverter.convert(user)
    }

    override fun hasLoggedIn(id: IdentityId): Boolean {
        val events = keycloak.realm(REALM).getEvents(
            listOf("LOGIN"),
            null,
            id.value,
            null,
            null,
            null,
            null,
            1
        )
        return events.isNotEmpty()
    }

    override fun getNewTeachers(since: LocalDate): List<Identity> = keycloak.realm(REALM)
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
        .mapNotNull { getUserById(IdentityId(value = it)) }

    override fun getUsers(): List<Identity> {
        val userCount = keycloak.realm(REALM).users().count()

        return keycloak
            .realm(REALM)
            .users()
            .list(0, userCount)
            .mapNotNull {
                try {
                    userConverter.convert(it)
                } catch (e: InvalidUserRepresentation) {
                    logger.warn { "Could not convert external keycloak user as email address is invalid" }
                    null
                }
            }
    }

    private fun getUserResource(id: String): UserResource {
        return keycloak.realm(REALM).users().get(id)
    }
}
