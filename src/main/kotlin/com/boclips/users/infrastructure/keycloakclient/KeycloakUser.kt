package com.boclips.users.infrastructure.keycloakclient

import org.keycloak.representations.idm.UserRepresentation

data class KeycloakUser(
    val username: String,
    val id: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val isVerified: Boolean? = null
) {
    companion object {
        fun from(
            user
            : UserRepresentation
        ) = KeycloakUser(
            id = user.id,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            username = user.username,
            isVerified = user.isEmailVerified
        )
    }
}
