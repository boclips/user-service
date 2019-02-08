package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.domain.model.users.IdentityProvider.Companion.TEACHERS_GROUP_NAME
import org.keycloak.representations.idm.AdminEventRepresentation
import java.time.LocalDate
import java.util.*

class KeycloakClientFake : IdentityProvider {
    val fakeUsers = hashMapOf(
        "b8dba3ac-c5a2-453e-b3d6-b1af1e48f027" to KeycloakUser(
            id = "b8dba3ac-c5a2-453e-b3d6-b1af1e48f027",
            username = "boclipper",
            firstName = "Little",
            lastName = "Bo",
            email = "engineering@boclips.com"
        ),
        "590784b2-c201-4ecb-b16f-9412af00bc69" to KeycloakUser(
            id = "590784b2-c201-4ecb-b16f-9412af00bc69",
            username = "Matt Jones",
            firstName = "Matt",
            lastName = "Jones",
            email = "matt+testing@boclips.com"
        ),
        "6ea9f529-1ec0-4fc9-8caa-ac1bb12eb3f3" to KeycloakUser(
            id = "6ea9f529-1ec0-4fc9-8caa-ac1bb12eb3f3",
            username = "notloggedin",
            firstName = "Not",
            lastName = "Logged in",
            email = "notloggedin@somewhere.com"
        )
    )

    val fakeGroups = hashMapOf<String, KeycloakGroup>(
    )

    data class GroupAssociation(val userId: String, val groupName: String)

    val fakeAdminEvents = mutableListOf<GroupAssociation>(

    )

    @Synchronized
    override fun getLastAdditionsToTeacherGroup(since: LocalDate): List<String> {
        return fakeAdminEvents.filter { it.groupName == TEACHERS_GROUP_NAME }.map { it.userId }
    }

    override fun createGroupIfDoesntExist(keycloakGroup: KeycloakGroup): KeycloakGroup {
        val createdGroup = keycloakGroup.copy(id = "${UUID.randomUUID()}")
        fakeGroups[createdGroup.id!!] = keycloakGroup
        return createdGroup
    }

    override fun addUserToGroup(userId: String, groupId: String) {
        val groupName = fakeGroups[groupId]!!.name
        fakeAdminEvents.add(GroupAssociation(userId, groupName))
    }

    private val hasLoggedIn = mutableMapOf<String, Boolean>()

    override fun hasLoggedIn(id: String): Boolean {
        return hasLoggedIn[id] ?: return false
    }

    override fun getUserByUsername(username: String): KeycloakUser {
        return KeycloakUser(
            id = username,
            username = username,
            firstName = "Little",
            lastName = "Bo",
            email = "$username@boclips.com"
        )
    }

    override fun deleteUserById(id: String): KeycloakUser {
        val user = fakeUsers[id]
        fakeUsers.remove(id)
        return user!!
    }

    override fun getUserById(id: String): KeycloakUser {
        return fakeUsers[id] ?: throw ResourceNotFoundException()
    }

    override fun createUserIfDoesntExist(user: KeycloakUser): KeycloakUser {
        val createdUser = user.copy(id = "${UUID.randomUUID()}")
        fakeUsers[createdUser.id!!] = createdUser
        return fakeUsers[createdUser.id] ?: throw RuntimeException("Something failed")
    }

    fun login(user: KeycloakUser) {
        hasLoggedIn[user.id!!] = true
    }

    @Synchronized
    fun clear() {
        fakeAdminEvents.clear()
        hasLoggedIn.clear()
        fakeUsers.clear()
        fakeGroups.clear()
    }
}
