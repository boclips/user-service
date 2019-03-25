package com.boclips.users.testsupport

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import java.util.UUID

class KeycloakClientFake : IdentityProvider {
    private val fakeUsers = hashMapOf<String, Identity>()

    override fun createUser(firstName: String, lastName: String, email: String, password: String): Identity {
        if (fakeUsers.values.filter { it.email == email }.isNotEmpty()) {
            throw UserAlreadyExistsException()
        }

        val id = UUID.randomUUID().toString()
        fakeUsers[id] = Identity(
            id = UserId(value = id),
            firstName = firstName,
            lastName = lastName,
            email = email,
            isVerified = false
        )
        return fakeUsers[id]!!
    }

    override fun getUserById(id: UserId): Identity? {
        return fakeUsers[id.value]
    }

    override fun getUsers(): List<Identity> {
        return fakeUsers.values.toList()
    }

    @Synchronized
    fun clear() {
        fakeUsers.clear()
    }

    fun createUser(identity: Identity): Identity {
        fakeUsers[identity.id.value] = identity
        return identity
    }
}
