package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.IdentityProvider
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.UserResource
import org.keycloak.representations.idm.UserRepresentation
import java.time.LocalDate
import javax.ws.rs.core.Response

class KeycloakClient(properties: KeycloakProperties) : IdentityProvider {

    companion object {
        const val REALM = "teachers"
    }

    private val keycloak = Keycloak.getInstance(
            properties.url,
            "master",
            properties.username,
            properties.password,
            "admin-cli"
    )

    override fun getUserById(id: String): KeycloakUser {
        val user: UserRepresentation?
        try {
            user = getUserResource(id).toRepresentation()
        } catch (e: javax.ws.rs.NotFoundException) {
            throw ResourceNotFoundException()
        }

        return KeycloakUser(
                id = user.id,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                username = user.username
        )
    }

    override fun getUserByUsername(username: String): KeycloakUser {
        val user = keycloak.realm(REALM).users().search(username)
                .first { it.username == username }

        return KeycloakUser(
                id = user.id,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                username = user.username
        )
    }

    override fun createUser(user: KeycloakUser): KeycloakUser {
        val userRepresentation = UserRepresentation()
        userRepresentation.username = user.username
        userRepresentation.firstName = user.firstName
        userRepresentation.lastName = user.lastName
        userRepresentation.email = user.email

        val response = keycloak.realm(REALM).users().create(userRepresentation)
        if (response.statusInfo.toEnum() != Response.Status.CREATED) {
            throw RuntimeException("Could not create user")
        }

        return getUserByUsername(user.username)
    }

    override fun deleteUserById(id: String): KeycloakUser {
        val user = getUserById(id)

        val response = keycloak.realm(REALM).users().delete(id)

        if (response.statusInfo.toEnum() != Response.Status.NO_CONTENT) {
            throw RuntimeException("Could not delete user")
        }

        return user
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

    override fun getLastLoginUserIds(client: String, since: LocalDate) = keycloak.realm(REALM)
            .getEvents(listOf("LOGIN"), client, null, since.toString(), null, null, null, 9999)
            .map { it.userId }


    private fun getUserResource(id: String): UserResource {
        return keycloak.realm(REALM).users().get(id)
    }
}