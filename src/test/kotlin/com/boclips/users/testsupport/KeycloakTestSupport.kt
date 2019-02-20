package com.boclips.users.testsupport

import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.infrastructure.keycloak.LowLevelKeycloakClient
import com.boclips.users.infrastructure.keycloak.client.KeycloakClient
import com.boclips.users.infrastructure.keycloak.client.KeycloakClient.Companion.REALM
import com.boclips.users.infrastructure.keycloak.client.KeycloakGroup
import com.boclips.users.infrastructure.keycloak.client.exceptions.ResourceNotFoundException
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.UserRepresentation
import javax.ws.rs.core.Response

class KeycloakTestSupport(
    private val keycloakInstance: Keycloak,
    private val identityProvider: IdentityProvider
) : LowLevelKeycloakClient {

    override fun deleteUserById(id: IdentityId): Identity {
        val user = identityProvider.getUserById(id)

        val response = keycloakInstance.realm(KeycloakClient.REALM).users().delete(id.value)

        if (response.statusInfo.toEnum() != Response.Status.NO_CONTENT) {
            throw RuntimeException("Could not delete user")
        }

        return user ?: throw ResourceNotFoundException()
    }

    override fun createUser(user: Identity): Identity {
        val userRepresentation = UserRepresentation()
        userRepresentation.username = user.email.toLowerCase()
        userRepresentation.firstName = user.firstName
        userRepresentation.lastName = user.lastName
        userRepresentation.email = user.email.toLowerCase()

        val newUser = keycloakInstance.realm(KeycloakClient.REALM).users().create(userRepresentation)
        if (!newUser.isCreatedOrExists()) {
            throw RuntimeException("Could not create user ${user.email}")
        }

        return identityProvider.getUserByUsername(user.email)
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