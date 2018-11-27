package com.boclips.users.keycloakclient

import org.keycloak.admin.client.Keycloak

class KeycloakClient(val config: KeycloakConfig) : IdentityProvider {
    override fun getUser(id: String): KeycloakUser {
        var keycloak = Keycloak.getInstance(
                config.url,
                "master",
                config.username,
                config.password,
                "admin-cli"
        )

        val user = keycloak.realm("teachers").users().get(id).toRepresentation()

        return KeycloakUser(user.id, user.email, user.firstName, user.lastName, user.username)
    }
}