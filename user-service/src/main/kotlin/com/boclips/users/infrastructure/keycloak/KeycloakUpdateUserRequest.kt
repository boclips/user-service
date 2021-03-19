package com.boclips.users.infrastructure.keycloak

data class KeycloakUpdateUserRequest (
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String
)