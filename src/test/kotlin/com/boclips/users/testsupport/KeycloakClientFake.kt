package com.boclips.users.testsupport

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSessions
import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.SessionProvider
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import com.boclips.users.testsupport.factories.UserSourceFactory
import java.time.Instant
import java.util.UUID

class KeycloakClientFake : IdentityProvider, SessionProvider {
    private val fakeUsers = hashMapOf<String, Identity>()
    private var session: UserSessions = UserSessions(lastAccess = null)

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
            isVerified = false,
            associatedTo = UserSourceFactory.boclipsSample()
        )
        return fakeUsers[id]!!
    }

    override fun getUserById(id: UserId): Identity? {
        return fakeUsers[id.value]
    }

    override fun getUsers(): List<Identity> {
        return fakeUsers.values.toList()
    }

    override fun count(): Int {
        return fakeUsers.count()
    }

    override fun getUserSessions(id: UserId): UserSessions {
        return session
    }

    @Synchronized
    fun clear() {
        fakeUsers.clear()
    }

    fun createUser(identity: Identity): Identity {
        fakeUsers[identity.id.value] = identity
        return identity
    }

    fun addUserSession(time: Instant) {
        session = UserSessions(lastAccess = time)
    }
}
