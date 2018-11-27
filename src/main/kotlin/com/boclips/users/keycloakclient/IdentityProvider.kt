package com.boclips.users.keycloakclient

interface IdentityProvider {
    fun getUser(id: String): KeycloakUser
}
