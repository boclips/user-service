package com.boclips.users.keycloakclient

class KeycloakClientFake : IdentityProvider {
    override fun getUser(id: String): KeycloakUser {
        return KeycloakUser(
                id = "b8dba3ac-c5a2-453e-b3d6-b1af1e48f027",
                username = "boclipper",
                firstName = "Little",
                lastName = "Bo",
                email = "engineering@boclips.com"
        )
    }
}