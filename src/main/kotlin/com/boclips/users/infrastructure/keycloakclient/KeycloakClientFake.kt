package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.domain.model.users.KeycloakId
import com.boclips.users.domain.model.users.User
import java.time.LocalDateTime
import java.util.UUID

class KeycloakClientFake : IdentityProvider {
    val fakeUsers = hashMapOf(
        "b8dba3ac-c5a2-453e-b3d6-b1af1e48f027" to User(
            keycloakId = KeycloakId(value = "b8dba3ac-c5a2-453e-b3d6-b1af1e48f027"),
            firstName = "Little",
            lastName = "Bo",
            email = "engineering@boclips.com",
            subjects = "some user input",
            mixpanelId = null,
            activated = false,
            createdAt = LocalDateTime.now()
        ),
        "590784b2-c201-4ecb-b16f-9412af00bc69" to User(
            keycloakId = KeycloakId(value = "590784b2-c201-4ecb-b16f-9412af00bc69"),
            firstName = "Matt",
            lastName = "Jones",
            email = "matt+testing@boclips.com",
            subjects = "some user input",
            mixpanelId = null,
            activated = false,
            createdAt = LocalDateTime.now()
        ),
        "6ea9f529-1ec0-4fc9-8caa-ac1bb12eb3f3" to User(
            keycloakId = KeycloakId(value = "6ea9f529-1ec0-4fc9-8caa-ac1bb12eb3f3"),
            firstName = "Not",
            lastName = "Logged in",
            email = "notloggedin@somewhere.com",
            subjects = "some user input",
            mixpanelId = null,
            activated = false,
            createdAt = LocalDateTime.now()
        )
    )

    private val hasLoggedIn = mutableMapOf<String, Boolean>()

    private val registeredEvents = mutableMapOf<KeycloakId, LocalDateTime>()

    override fun getUserIdsRegisteredSince(since: LocalDateTime): List<KeycloakId> {
        return registeredEvents
            .filter { it.value.isAfter(since) }
            .keys
            .toList()
    }

    override fun getUsersRegisteredSince(since: LocalDateTime): List<User> {
        return getUserIdsRegisteredSince(since)
            .mapNotNull { fakeUsers[it.value] }
    }

    override fun hasLoggedIn(keycloakId: KeycloakId): Boolean {
        return hasLoggedIn[keycloakId.value] ?: return false
    }

    override fun deleteUserById(keycloakId: KeycloakId): User {
        val user = fakeUsers[keycloakId.value]
        fakeUsers.remove(keycloakId.value)
        return user!!
    }

    override fun getUserById(keycloakId: KeycloakId): User {
        return fakeUsers[keycloakId.value] ?: throw ResourceNotFoundException()
    }

    override fun createUserIfDoesntExist(user: User): User {
        val createdUser = user.copy(keycloakId = KeycloakId(value = "${UUID.randomUUID()}"))
        fakeUsers[createdUser.keycloakId.value] = createdUser
        return fakeUsers[createdUser.keycloakId.value] ?: throw RuntimeException("Something failed")
    }

    override fun getUsers(): List<User> {
        return fakeUsers.values.toList()
    }

    fun createUserWithId(user: User): User {
        fakeUsers[user.keycloakId.value] = user
        return fakeUsers[user.keycloakId.value] ?: throw RuntimeException("Could not create user")
    }

    fun login(user: KeycloakUser) {
        hasLoggedIn[user.id!!] = true
    }

    @Synchronized
    fun clear() {
        hasLoggedIn.clear()
        fakeUsers.clear()
    }

    fun createRegisteredEvents(keycloakId: KeycloakId) {
        registeredEvents[keycloakId] = LocalDateTime.now()
    }
}
