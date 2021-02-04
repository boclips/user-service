package com.boclips.users.testsupport

import com.boclips.users.domain.model.user.Identity
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.user.UserSessions
import com.boclips.users.domain.service.user.IdentityProvider
import com.boclips.users.domain.service.user.SessionProvider
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import com.boclips.users.testsupport.factories.IdentityFactory
import java.time.Instant
import java.time.ZonedDateTime
import java.util.UUID

class KeycloakClientFake : IdentityProvider, SessionProvider {
    private val fakeUsers = hashMapOf<String, Identity>()
    private var session: UserSessions =
        UserSessions(lastAccess = null)

    override fun createIdentity(email: String, password: String, role: String?): Identity {
        if (fakeUsers.values.filter { it.email == email }.isNotEmpty()) {
            throw UserAlreadyExistsException()
        }

        val id = UUID.randomUUID().toString()
        fakeUsers[id] = IdentityFactory.sample(
            id = id,
            username = email,
            roles = listOfNotNull(role),
            createdAt = ZonedDateTime.now()
        )
        return fakeUsers[id]!!
    }

    override fun getIdentitiesById(id: UserId): Identity? {
        return fakeUsers[id.value]
    }

    override fun getIdentity(): Sequence<Identity> {
        return fakeUsers.values.asSequence()
    }

    override fun count(): Int {
        return fakeUsers.count()
    }

    override fun getUserSessions(id: UserId): UserSessions {
        return session
    }

    override fun deleteIdentity(id: UserId) {
        fakeUsers.remove(id.value)
    }

    @Synchronized
    fun clear() {
        fakeUsers.clear()
    }

    fun createAccount(identity: Identity): Identity {
        fakeUsers[identity.id.value] = identity
        return identity
    }

    fun addUserSession(time: Instant) {
        session = UserSessions(lastAccess = time)
    }
}
