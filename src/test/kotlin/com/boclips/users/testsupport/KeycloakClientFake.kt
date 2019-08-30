package com.boclips.users.testsupport

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSessions
import com.boclips.users.domain.service.AccountProvider
import com.boclips.users.domain.service.SessionProvider
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import com.boclips.users.testsupport.factories.UserSourceFactory
import java.time.Instant
import java.util.UUID

class KeycloakClientFake : AccountProvider, SessionProvider {
    private val fakeUsers = hashMapOf<String, Account>()
    private var session: UserSessions = UserSessions(lastAccess = null)

    override fun createAccount(email: String, password: String): Account {
        if (fakeUsers.values.filter { it.email == email }.isNotEmpty()) {
            throw UserAlreadyExistsException()
        }

        val id = UUID.randomUUID().toString()
        fakeUsers[id] = Account(
            id = UserId(value = id),
            username = email,
            organisationType = UserSourceFactory.boclipsSample()
        )
        return fakeUsers[id]!!
    }

    override fun getAccountById(id: UserId): Account? {
        return fakeUsers[id.value]
    }

    override fun getAccounts(): Sequence<Account> {
        return fakeUsers.values.asSequence()
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

    fun createAccount(account: Account): Account {
        fakeUsers[account.id.value] = account
        return account
    }

    fun addUserSession(time: Instant) {
        session = UserSessions(lastAccess = time)
    }
}
