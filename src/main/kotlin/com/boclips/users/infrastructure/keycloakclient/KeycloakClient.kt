package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.domain.model.users.IdentityProvider.Companion.TEACHERS_GROUP_NAME
import com.jayway.jsonpath.JsonPath.parse
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.UserResource
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.UserRepresentation
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.ws.rs.core.Response

class KeycloakClient(properties: KeycloakProperties) : IdentityProvider {
    companion object {
        const val TEACHERS_REALM = "teachers"
    }

    private val keycloak = Keycloak.getInstance(
            properties.url,
            TEACHERS_REALM,
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
                username = user.username,
                id = user.id,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                isEmailVerified = user.isEmailVerified,
                createdAccountAt = getLocalDateTimeOfTimestamp(user.createdTimestamp)
        )
    }

    override fun getAllUsers(): List<KeycloakUser> {
        return keycloak.realm(TEACHERS_REALM)
                .users()
                .list()
                .map { user ->
                    KeycloakUser(
                            id = user.id,
                            email = user.email,
                            firstName = user.firstName,
                            lastName = user.lastName,
                            username = user.username,
                            isEmailVerified = user.isEmailVerified,
                            createdAccountAt = getLocalDateTimeOfTimestamp(user.createdTimestamp)
                    )
                }
    }

    override fun getUserByUsername(username: String): KeycloakUser {
        val user = keycloak.realm(TEACHERS_REALM).users().search(username)
                .first { it.username == username }

        return KeycloakUser(
                username = user.username,
                id = user.id,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                isEmailVerified = user.isEmailVerified,
                createdAccountAt = getLocalDateTimeOfTimestamp(user.createdTimestamp)
        )
    }

    override fun createUserIfDoesntExist(user: KeycloakUser): KeycloakUser {
        val userRepresentation = UserRepresentation()
        userRepresentation.username = user.username
        userRepresentation.firstName = user.firstName
        userRepresentation.lastName = user.lastName
        userRepresentation.email = user.email

        val newUser = keycloak.realm(TEACHERS_REALM).users().create(userRepresentation)
        if (!newUser.isCreatedOrExists()) {
            throw RuntimeException("Could not create user ${user.username}")
        }

        return getUserByUsername(user.username)
    }

    override fun deleteUserById(id: String): KeycloakUser {
        val user = getUserById(id)

        val response = keycloak.realm(TEACHERS_REALM).users().delete(id)

        if (response.statusInfo.toEnum() != Response.Status.NO_CONTENT) {
            throw RuntimeException("Could not delete user")
        }

        return user
    }

    override fun hasLoggedIn(id: String): Boolean {
        val events = keycloak.realm(TEACHERS_REALM).getEvents(
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

    override fun getLastAdditionsToTeacherGroup(since: LocalDate) = keycloak.realm(TEACHERS_REALM)
            .getAdminEvents(listOf("CREATE"), null, null, null, null, null, since.toString(), null, 0, 9999)

            .filter { it.resourceType == "GROUP_MEMBERSHIP" }
            .filter { parse(it.representation).read<String>("$.name") == TEACHERS_GROUP_NAME }
            .map { it.resourcePath.substringAfter("users/").substringBefore("/") }

    fun getUserResource(id: String): UserResource {
        return keycloak.realm(TEACHERS_REALM).users().get(id)
    }

    override fun createGroupIfDoesntExist(keycloakGroup: KeycloakGroup): KeycloakGroup {
        val newGroup = keycloak.realm(TEACHERS_REALM).groups().add(GroupRepresentation().apply { name = keycloakGroup.name })

        if (!newGroup.isCreatedOrExists()) {
            throw RuntimeException("Could not create group ${keycloakGroup.name}")
        }

        return getGroupByGroupName(keycloakGroup.name)
    }

    private fun Response.isCreatedOrExists() =
            this.statusInfo.toEnum() in listOf(Response.Status.CREATED, Response.Status.CONFLICT)

    private fun getGroupByGroupName(groupName: String): KeycloakGroup {
        val group = keycloak.realm(TEACHERS_REALM).groups().groups().first { it.name == groupName }

        return KeycloakGroup(
                id = group.id,
                name = group.name
        )
    }

    override fun addUserToGroup(userId: String, groupId: String) {
        getUserResource(userId).joinGroup(groupId)
    }

    private fun getLocalDateTimeOfTimestamp(timestamp: Long) =
            LocalDateTime.ofInstant(Timestamp(timestamp).toInstant(), ZoneOffset.UTC)
}
