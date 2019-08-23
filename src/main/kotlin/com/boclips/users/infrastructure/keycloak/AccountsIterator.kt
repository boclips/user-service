package com.boclips.users.infrastructure.keycloak

import org.keycloak.representations.idm.UserRepresentation

class AccountsIterator(private val keycloakWrapper: KeycloakWrapper) : Iterable<UserRepresentation> {
    override fun iterator(): Iterator<UserRepresentation> {
        val end = keycloakWrapper.countUsers()
        return InnerIterator(0, end)
    }

    class InnerIterator(private val start: Int, private val end: Int) : Iterator<UserRepresentation> {
        override fun hasNext(): Boolean {
            return false
        }

        override fun next(): UserRepresentation {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}