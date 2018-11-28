package com.boclips.users.infrastructure.keycloakclient

import java.util.*
import javax.ws.rs.NotFoundException

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

    private val hasLoggedIn = mapOf<String, Boolean>()

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
        return fakeUsers[id] ?: throw NotFoundException()
    }

    override fun createUser(user: KeycloakUser): KeycloakUser {
        val createdUser = user.copy(id = "${UUID.randomUUID()}")
        fakeUsers[createdUser.id!!] = createdUser
        return fakeUsers[createdUser.id] ?: throw RuntimeException("Something failed")
    }
}
