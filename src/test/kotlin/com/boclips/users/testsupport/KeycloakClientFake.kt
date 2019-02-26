package com.boclips.users.testsupport

import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.service.IdentityProvider
import java.time.LocalDate

class KeycloakClientFake : IdentityProvider {
    private val fakeUsers = hashMapOf<String, Identity>()

    @Synchronized
    override fun getNewTeachers(since: LocalDate): List<Identity> {
        return fakeUsers.values.toList()
    }

    override fun getUserById(id: IdentityId): Identity? {
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