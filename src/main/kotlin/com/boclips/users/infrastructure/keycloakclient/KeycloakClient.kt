package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.domain.model.users.KeycloakId
import com.boclips.users.domain.model.users.User
import mu.KLogging
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.UserResource
import org.keycloak.representations.idm.EventRepresentation
import org.keycloak.representations.idm.UserRepresentation
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.ws.rs.core.Response

open class KeycloakClient(
    properties: KeycloakProperties
) : IdentityProvider {
    companion object : KLogging() {
        const val REALM = "boclips"
    }

    private val keycloak = Keycloak.getInstance(
        properties.url,
        REALM,
        properties.username,
        properties.password,
        "admin-cli"
    )

    override fun getUserById(keycloakId: KeycloakId): User {
        val user: UserRepresentation?
        try {
            user = getUserResource(keycloakId.value).toRepresentation()
        } catch (e: javax.ws.rs.NotFoundException) {
            throw ResourceNotFoundException()
        }

        return toUser(user) ?: throw InvalidResourceException()
    }

    override fun createUserIfDoesntExist(user: User): User {
        val userRepresentation = UserRepresentation()
        userRepresentation.username = user.email
        userRepresentation.firstName = user.firstName
        userRepresentation.lastName = user.lastName
        userRepresentation.email = user.email

        val newUser = keycloak.realm(REALM).users().create(userRepresentation)
        if (!newUser.isCreatedOrExists()) {
            throw RuntimeException("Could not create user ${user.email}")
        }

        return getUserByEmail(user.email)
    }

    override fun deleteUserById(keycloakId: KeycloakId): User {
        val user = getUserById(keycloakId)

        val response = keycloak.realm(REALM).users().delete(user.keycloakId.value)

        if (response.statusInfo.toEnum() != Response.Status.NO_CONTENT) {
            throw RuntimeException("Could not delete user")
        }

        return user
    }

    override fun hasLoggedIn(keycloakId: KeycloakId): Boolean {
        val events = keycloak.realm(REALM).getEvents(
            listOf("LOGIN"),
            null,
            keycloakId.value,
            null,
            null,
            null,
            null,
            1
        )
        return events.isNotEmpty()
    }

    override fun getUserIdsRegisteredSince(since: LocalDateTime) =
        getRegisterEvents(since.minusDays(1).toLocalDate())
            .filter {
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(it.time),
                    ZoneOffset.UTC
                )
                    .isAfter(since)
            }
            .map { KeycloakId(value = it.userId) }

    override fun getUsersRegisteredSince(since: LocalDateTime) =
        getUserIdsRegisteredSince(since).map { getUserById(it) }

    override fun getUsers(): List<User> {
        val userCount = keycloak.realm(REALM).users().count()

        keycloak.realm(REALM)

        return keycloak.realm(REALM).users().list(0, userCount).mapNotNull { toUser(it) }
    }

    open fun getRegisterEvents(since: LocalDate): List<EventRepresentation> = keycloak.realm(REALM)
        .getEvents(listOf("REGISTER"), null, null, since.minusDays(1).toString(), null, null, 0, 9999)

    private fun toUser(user: UserRepresentation): User? {
        val ofEpochMilli = Instant.ofEpochMilli(user.createdTimestamp)

        return try {
            User(
                keycloakId = KeycloakId(value = user.id),
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email,
                activated = user.isEmailVerified,
                createdAt = LocalDateTime.ofInstant(ofEpochMilli, ZoneOffset.UTC),
                mixpanelId = user.attributes?.get("mixpanelDistinctId")?.first(),
                subjects = user.attributes?.get("subjects")?.first() ?: ""
            )
        } catch (ise: IllegalStateException) {
            logger.warn(ise) {
                "Invalid user: ${user.id}"
            }

            null
        }
    }

    private fun getUserResource(id: String): UserResource {
        return keycloak.realm(REALM).users().get(id)
    }

    private fun getUserByEmail(email: String): User {
        val user = keycloak.realm(REALM).users().search(email)
            .first { it.email == email }

        return toUser(user) ?: throw InvalidResourceException()
    }

    private fun Response.isCreatedOrExists() =
        this.statusInfo.toEnum() in listOf(Response.Status.CREATED, Response.Status.CONFLICT)
}
