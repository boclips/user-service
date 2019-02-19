package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.infrastructure.keycloakclient.KeycloakClient.Companion.TEACHERS_GROUP_NAME
import java.time.LocalDate
import java.util.UUID

class KeycloakClientFake : IdentityProvider, LowLevelKeycloakClient {
    private val fakeUsers = hashMapOf(
        "b8dba3ac-c5a2-453e-b3d6-b1af1e48f027" to Identity(
            id = IdentityId(value = "b8dba3ac-c5a2-453e-b3d6-b1af1e48f027"),
            firstName = "Little",
            lastName = "Bo",
            email = "engineering@boclips.com",
            isVerified = false
        ),
        "590784b2-c201-4ecb-b16f-9412af00bc69" to Identity(
            id = IdentityId(value = "590784b2-c201-4ecb-b16f-9412af00bc69"),
            firstName = "Matt",
            lastName = "Jones",
            email = "matt+testing@boclips.com",
            isVerified = false
        ),
        "6ea9f529-1ec0-4fc9-8caa-ac1bb12eb3f3" to Identity(
            id = IdentityId(value = "6ea9f529-1ec0-4fc9-8caa-ac1bb12eb3f3"),
            firstName = "Not",
            lastName = "Logged in",
            email = "notloggedin@somewhere.com",
            isVerified = false
        )
    )

    data class GroupAssociation(val userId: String, val groupName: String)

    val fakeGroups = hashMapOf(
        "teachers-id" to KeycloakGroup("teachers-id", TEACHERS_GROUP_NAME)
    )

    val fakeAdminEvents = mutableListOf<GroupAssociation>()

    @Synchronized
    override fun getNewTeachers(since: LocalDate): List<Identity> {
        return fakeAdminEvents.filter { it.groupName == TEACHERS_GROUP_NAME }
            .mapNotNull { getUserById(id = IdentityId(value = it.userId)) }
    }

    override fun hasLoggedIn(id: IdentityId): Boolean {
        return hasLoggedIn[id.value] ?: return false
    }

    override fun getUserByUsername(username: String): Identity {
        return Identity(
            id = IdentityId(value = username),
            firstName = "Little",
            lastName = "Bo",
            email = "$username@boclips.com",
            isVerified = false
        )
    }

    override fun getUserById(id: IdentityId): Identity? {
        return fakeUsers[id.value]
    }

    override fun getUsers(): List<Identity> {
        return fakeUsers.values.toList()
    }

    fun login(user: Identity) {
        hasLoggedIn[user.id.value] = true
    }

    @Synchronized
    fun clear() {
        fakeAdminEvents.clear()
        hasLoggedIn.clear()
        fakeUsers.clear()
        fakeGroups.clear()
    }

    override fun createGroup(keycloakGroup: KeycloakGroup): KeycloakGroup {
        val createdGroup = keycloakGroup.copy(id = "${UUID.randomUUID()}")
        fakeGroups[createdGroup.id!!] = keycloakGroup
        return createdGroup
    }

    override fun createUser(user: Identity): Identity {
        if (fakeUsers.containsKey(user.id.value)) {
            return user
        }

        val createdUser = user.copy(id = IdentityId(value = "${UUID.randomUUID()}"))
        fakeUsers[createdUser.id.value] = createdUser

        return fakeUsers[createdUser.id.value]!!
    }

    override fun deleteUserById(identityId: IdentityId): Identity {
        val id = identityId.value
        val user = fakeUsers[id]
        fakeUsers.remove(id)
        return user!!
    }

    override fun addUserToGroup(userId: String, groupId: String) {
        val groupName = fakeGroups[groupId]!!.name
        fakeAdminEvents.add(GroupAssociation(userId, groupName))
    }

    private val hasLoggedIn = mutableMapOf<String, Boolean>()
}
