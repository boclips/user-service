package com.boclips.users.testsupport

import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.infrastructure.keycloakclient.KeycloakClient
import com.boclips.users.infrastructure.keycloakclient.KeycloakClient.Companion.REALM
import com.boclips.users.infrastructure.keycloakclient.KeycloakGroup
import com.boclips.users.infrastructure.keycloakclient.KeycloakUser
import com.boclips.users.infrastructure.keycloakclient.LowLevelKeycloakClient
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.UserRepresentation
import javax.ws.rs.core.Response

class KeycloakTestSupport(
    private val keycloakInstance: Keycloak,
    private val identityProvider: IdentityProvider
) : LowLevelKeycloakClient {

    override fun deleteUserById(id: String): KeycloakUser {
        val user = identityProvider.getUserById(id)

        val response = keycloakInstance.realm(KeycloakClient.REALM).users().delete(id)

        if (response.statusInfo.toEnum() != Response.Status.NO_CONTENT) {
            throw RuntimeException("Could not delete user")
        }

        return user
    }

    override fun createUser(user: KeycloakUser): KeycloakUser {
        val userRepresentation = UserRepresentation()
        userRepresentation.username = user.username
        userRepresentation.firstName = user.firstName
        userRepresentation.lastName = user.lastName
        userRepresentation.email = user.email

        val newUser = keycloakInstance.realm(KeycloakClient.REALM).users().create(userRepresentation)
        if (!newUser.isCreatedOrExists()) {
            throw RuntimeException("Could not create user ${user.username}")
        }

        return identityProvider.getUserByUsername(user.username)
    }

    override fun createGroup(keycloakGroup: KeycloakGroup): KeycloakGroup {
        val newGroup =
            keycloakInstance.realm(REALM).groups().add(GroupRepresentation().apply { name = keycloakGroup.name })

        if (!newGroup.isCreatedOrExists()) {
            throw RuntimeException("Could not create group ${keycloakGroup.name}")
        }

        return getGroupByGroupName(keycloakGroup.name)
    }

    override fun addUserToGroup(userId: String, groupId: String) {
        keycloakInstance.realm(REALM).users().get(userId).joinGroup(groupId)
    }

    fun Response.isCreatedOrExists() =
        this.statusInfo.toEnum() in listOf(Response.Status.CREATED, Response.Status.CONFLICT)

    fun getGroupByGroupName(groupName: String): KeycloakGroup {
        val group = keycloakInstance.realm(REALM).groups().groups().first { it.name == groupName }

        return KeycloakGroup(
            id = group.id,
            name = group.name
        )
    }
}