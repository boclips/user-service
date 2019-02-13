package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.domain.model.users.IdentityProvider.Companion.TEACHERS_GROUP_NAME
import com.jayway.jsonpath.JsonPath.parse
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.UserResource
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.UserRepresentation
import java.time.LocalDate
import javax.ws.rs.core.Response

class KeycloakClient(properties: KeycloakProperties) : IdentityProvider {

    companion object {
        const val REALM = "boclips"
    }

    private val keycloak = Keycloak.getInstance(
        properties.url,
        REALM,
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

        return KeycloakUser.from(user)
    }

    override fun getUserByUsername(username: String): KeycloakUser {
        val user = keycloak.realm(REALM).users().search(username)
            .first { it.username == username }

        return KeycloakUser.from(user)
    }

    override fun createUserIfDoesntExist(user: KeycloakUser): KeycloakUser {
        val userRepresentation = UserRepresentation()
        userRepresentation.username = user.username
        userRepresentation.firstName = user.firstName
        userRepresentation.lastName = user.lastName
        userRepresentation.email = user.email

        val newUser = keycloak.realm(REALM).users().create(userRepresentation)
        if (!newUser.isCreatedOrExists()) {
            throw RuntimeException("Could not create user ${user.username}")
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

    override fun getLastAdditionsToTeacherGroup(since: LocalDate) = keycloak.realm(REALM)
        .getAdminEvents(listOf("CREATE"), null, null, null, null, null, since.toString(), null, 0, 9999)
        .filter { it.resourceType == "GROUP_MEMBERSHIP" }
        .filter { parse(it.representation).read<String>("$.name") == TEACHERS_GROUP_NAME }
        .map { it.resourcePath.substringAfter("users/").substringBefore("/") }

    override fun createGroupIfDoesntExist(keycloakGroup: KeycloakGroup): KeycloakGroup {
        val newGroup = keycloak.realm(REALM).groups().add(GroupRepresentation().apply { name = keycloakGroup.name })

        if (!newGroup.isCreatedOrExists()) {
            throw RuntimeException("Could not create group ${keycloakGroup.name}")
        }

        return getGroupByGroupName(keycloakGroup.name)
    }

    override fun addUserToGroup(userId: String, groupId: String) {
        getUserResource(userId).joinGroup(groupId)
    }

    override fun getUsers(): List<KeycloakUser> {
        val userCount = keycloak.realm(REALM).users().count()

        return keycloak.realm(REALM).users().list(0, userCount).map { KeycloakUser.from(it) }
    }

    private fun getUserResource(id: String): UserResource {
        return keycloak.realm(REALM).users().get(id)
    }

    private fun Response.isCreatedOrExists() =
        this.statusInfo.toEnum() in listOf(Response.Status.CREATED, Response.Status.CONFLICT)

    private fun getGroupByGroupName(groupName: String): KeycloakGroup {
        val group = keycloak.realm(REALM).groups().groups().first { it.name == groupName }

        return KeycloakGroup(
            id = group.id,
            name = group.name
        )
    }
}
